package cz.utb.fai.dgapp.domain

interface CourseRepository {
    /**
     * Retrieves courses, either from local cache or remote API.
     * Searches courses by name or description using the provided query string.
     *
     * @param searchQuery The text to search for (delegated to MongoDB text index).
     * @param forceRefresh If true, skips local data and fetches from remote.
     * @return List of matching Course domain models.
     */
    suspend fun getCourses(searchQuery: String = "", forceRefresh: Boolean = false): List<Course>

    /**
     * Retrieves a single Course by its ID.
     */
    suspend fun getCourseById(id: String): Course

    /**
     * Saves a new Course to the remote API and updates the local cache.
     */
    suspend fun createCourse(course: Course): Course
}