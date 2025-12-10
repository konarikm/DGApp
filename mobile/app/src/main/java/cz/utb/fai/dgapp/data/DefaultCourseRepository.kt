package cz.utb.fai.dgapp.data

import cz.utb.fai.dgapp.data.local.CourseLocalDataSource
import cz.utb.fai.dgapp.data.remote.CourseRemoteDataSource
import cz.utb.fai.dgapp.domain.CourseRepository
import cz.utb.fai.dgapp.data.mappers.toDomain
import cz.utb.fai.dgapp.data.mappers.toEntity
import cz.utb.fai.dgapp.data.mappers.toApiDto
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
        val apiDto = course.toApiDto()

        // 2. Call remote API to save the new course
        val newCourseDto = remoteDataSource.createCourse(apiDto)

        // 3. Convert response back to Domain model (which now includes the new ID)
        val newCourseDomain = newCourseDto.toDomain()

        return newCourseDomain
    }
}