package cz.utb.fai.dgapp.data

import cz.utb.fai.dgapp.data.local.CourseLocalDataSource
import cz.utb.fai.dgapp.data.remote.CourseRemoteDataSource
import cz.utb.fai.dgapp.domain.CourseRepository
import cz.utb.fai.dgapp.data.mappers.toDomain
import cz.utb.fai.dgapp.data.mappers.toEntity
import cz.utb.fai.dgapp.data.mappers.toApiDto
import cz.utb.fai.dgapp.data.mappers.toCreateApiDto
import cz.utb.fai.dgapp.domain.Course

class DefaultCourseRepository(
    private val remoteDataSource: CourseRemoteDataSource,
    private val localDataSource: CourseLocalDataSource,
) : CourseRepository {
    override suspend fun getCourses(searchQuery: String, forceRefresh: Boolean): List<Course>{
        if (searchQuery.isNotBlank()) {
            val remoteDtos = remoteDataSource.getCourses(searchQuery)
            return remoteDtos.map { it.toDomain() }
        }

        val local = localDataSource.getCourses()
        val shouldRefresh = forceRefresh || local.isEmpty()

        return if (shouldRefresh) {
            val remoteDtos = remoteDataSource.getCourses(searchQuery = "")
            val domainCourses = remoteDtos.map{ it.toDomain() }

            localDataSource.saveCourses(domainCourses.map{ it.toEntity() })

            domainCourses
        } else {
            local.map{ it.toDomain() }
        }
    }

    override suspend fun createCourse(course: Course): Course {
        // 1. Convert Domain model to API DTO
        // NOTE: The server needs CourseApiDto
        val apiDto = course.toCreateApiDto()

        // 2. Call remote API to save the new course
        val newCourseDto = remoteDataSource.createCourse(apiDto)

        // 3. Convert response back to Domain model (which now includes the new ID)
        val newCourseDomain = newCourseDto.toDomain()

        return newCourseDomain
    }

    override suspend fun getCourseById(id: String): Course {
        // 1. Try to get from local cache
        val localEntity = localDataSource.getCourseById(id)

        return if (localEntity != null) {
            // Found in cache, convert and return
            localEntity.toDomain()
        } else {
            // 2. Not found locally, fetch from remote
            val remoteDto = remoteDataSource.getCourseById(id)

            // 3. Convert DTO to Domain
            val domainCourse = remoteDto.toDomain()

            // 4. Cache the new data
            localDataSource.saveCourse(domainCourse.toEntity())

            return domainCourse
        }
    }

    override suspend fun updateCourse(course: Course): Course {
        // 1. Convert Domain model to API DTO
        // Since it's an update, the DTO must contain the ID.
        val apiDto = course.toApiDto()

        // 2. Call remote API to update the course (PUT request)
        // We assume remoteDataSource has an updateCourse method accepting the DTO
        val updatedCourseDto = remoteDataSource.updateCourse(apiDto)

        // 3. Convert response back to Domain model
        val updatedCourseDomain = updatedCourseDto.toDomain()

        // 4. Update local cache
        localDataSource.saveCourse(updatedCourseDomain.toEntity())

        return updatedCourseDomain
    }

    override suspend fun deleteCourse(id: String) {
        // 1. Call remote API to delete the course
        remoteDataSource.deleteCourse(id)

        // 2. Remove from local cache
        localDataSource.deleteCourse(id)
    }
}