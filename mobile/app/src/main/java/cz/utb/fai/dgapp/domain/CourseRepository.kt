package cz.utb.fai.dgapp.domain

interface CourseRepository {
    suspend fun getCourses(searchQuery: String = "", forceRefresh: Boolean = false): List<Course>
}