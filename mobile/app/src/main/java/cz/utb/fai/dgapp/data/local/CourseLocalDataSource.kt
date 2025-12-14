package cz.utb.fai.dgapp.data.local


/**
 * Local datasource implementation using Room database (CourseDao).
 * All methods now delegate directly to the injected DAO.
 */
class CourseLocalDataSource(
    private val courseDao: CourseDao
) {

    /**
     * Retrieves all courses from the Room database.
     */
    suspend fun getCourses(): List<CourseEntity> {
        return courseDao.getAllCourses()
    }

    /**
     * Retrieves a specific course by ID from the Room database.
     */
    suspend fun getCourseById(id: String): CourseEntity? {
        return courseDao.getCourseById(id)
    }

    /**
     * Saves a list of courses to the Room database (replaces on conflict).
     */
    suspend fun saveCourses(courses: List<CourseEntity>) {
        courseDao.insertAll(courses)
    }

    /**
     * Saves a single course to the Room database.
     */
    suspend fun saveCourse(course: CourseEntity) {
        courseDao.insertCourse(course)
    }

    /**
     * Deletes a course by ID from the Room database.
     */
    suspend fun deleteCourse(id: String) {
        courseDao.deleteCourse(id)
    }
}