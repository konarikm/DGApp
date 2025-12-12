package cz.utb.fai.dgapp.data.local

import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap


// TODO
// implementovat reálnou instanci Room databáze

/**
 * Type Converters instance to handle JSON serialization for List<Int>.
 */
private val converters = Converters()

/**
 * Fake "Room" datasource – drží data jen v paměti.
 */
class CourseLocalDataSource {
    // In-memory cache to simulate the Room database table.
    private val cache: ConcurrentHashMap<String, CourseEntity> = ConcurrentHashMap()

    // --- Mock Data Initialization ---
    // These values must be consistent with CourseEntity structure (JSON storage for par values)
    init {
        val MOCK_LAGUNA = CourseEntity(
            id = "6918e1e390ea5c4759d49dcc",
            name = "DiscgolfPark Laguna Přerov",
            location = "Přerov, Czechia",
            description = "Hřiště ideální pro začátečníky rozprostírající se v okolí přerovské laguny",
            numberOfHoles = 9,
            parValuesJson = converters.fromIntList(listOf(3, 3, 3, 3, 3, 3, 3, 3, 3))
        )
        val MOCK_TUCIN = CourseEntity(
            id = "6918e20c90ea5c4759d49dce",
            name = "DiscgolfPark Tučín",
            location = "Tučín, Czechia",
            description = null,
            numberOfHoles = 10,
            parValuesJson = converters.fromIntList(listOf(3, 3, 3, 3, 3, 3, 3, 4, 3, 3))
        )
        cache[MOCK_LAGUNA.id] = MOCK_LAGUNA
        cache[MOCK_TUCIN.id] = MOCK_TUCIN
    }

    // Implements CourseRepository.getCourses logic (returns all from cache)
    suspend fun getCourses(): List<CourseEntity> {
        delay(100) // Simulate read latency
        return cache.values.toList()
    }

    // NEW: Implements the required method for DefaultCourseRepository.getCourseById
    suspend fun getCourseById(id: String): CourseEntity? {
        delay(100) // Simulate read latency
        return cache[id]
    }

    // Implements CourseRepository.saveCourses logic (saves a list)
    suspend fun saveCourses(courses: List<CourseEntity>) {
        delay(100) // Simulate write latency
        courses.forEach { cache[it.id] = it }
    }

    // Implements CourseRepository.saveCourse logic (saves a single course)
    suspend fun saveCourse(course: CourseEntity) {
        delay(100) // Simulate write latency
        cache[course.id] = course
    }
}