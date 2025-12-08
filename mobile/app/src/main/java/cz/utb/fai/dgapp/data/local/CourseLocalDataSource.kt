package cz.utb.fai.dgapp.data.local

import kotlinx.coroutines.delay

/**
 * Fake "Room" datasource – drží data jen v paměti.
 */

// TODO
// implementovat reálnou instanci Room databáze

class CourseLocalDataSource {
    private var cache: List<CourseEntity> = emptyList()

    suspend fun getCourses(): List<CourseEntity> {
        delay(200)
        return cache
    }

    suspend fun saveCourses(courses: List<CourseEntity>) {
        delay(200)
        cache = courses
    }
}