package cz.utb.fai.dgapp.domain

interface CourseRepository {
    suspend fun getCourses(forceRefresh: Boolean = false): List<Course>
}