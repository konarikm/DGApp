package cz.utb.fai.dgapp.data

import cz.utb.fai.dgapp.data.local.CourseLocalDataSource
import cz.utb.fai.dgapp.data.remote.CourseRemoteDataSource
import cz.utb.fai.dgapp.domain.CourseRepository
import cz.utb.fai.dgapp.data.mappers.toDomain
import cz.utb.fai.dgapp.data.mappers.toEntity
import cz.utb.fai.dgapp.domain.Course

class DefaultCourseRepository(
    private val remoteDataSource: CourseRemoteDataSource,
    private val localDataSource: CourseLocalDataSource,
) : CourseRepository {
    override suspend fun getCourses(forceRefresh: Boolean): List<Course>{
        val local = localDataSource.getCourses()

        val shouldRefresh = forceRefresh || local.isEmpty()

        return if (shouldRefresh) {
            val remoteDtos = remoteDataSource.getCourses()
            val domainCourses = remoteDtos.map{ it.toDomain() }

            val entities = domainCourses.map{ it.toEntity() }
            localDataSource.saveCourses(entities)

            domainCourses
        } else {
            local.map{ it.toDomain() }
        }
    }
}