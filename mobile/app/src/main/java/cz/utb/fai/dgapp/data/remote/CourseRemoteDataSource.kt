package cz.utb.fai.dgapp.data.remote

import kotlinx.coroutines.delay

class CourseRemoteDataSource {
    private val MOCK_COURSE_LAGUNA = CourseApiDto(
        id = "6918e1e390ea5c4759d49dcc",
        name = "DiscgolfPark Laguna Přerov",
        location = "Přerov, Czechia",
        description = "Hřiště ideální pro začátečníky rozprostírající se v okolí přerovské laguny",
        numberOfHoles = 9,
        parValues = listOf(3, 3, 3, 3, 3, 3, 3, 3, 3)
    )

    private val MOCK_COURSE_TUCIN = CourseApiDto(
        id = "6918e20c90ea5c4759d49dce",
        name = "DiscgolfPark Tučín",
        location = "Tučín, Czechia",
        description = null,
        numberOfHoles = 10,
        parValues = listOf(3, 3, 3, 3, 3, 3, 3, 4, 3, 3)
    )

    private val allCourses = listOf(MOCK_COURSE_LAGUNA, MOCK_COURSE_TUCIN)

    // FAKE REST - simulace síťového volání
    suspend fun getCourses(searchQuery: String): List<CourseApiDto> {
        delay(500)

        if (searchQuery.isBlank()) {
            return allCourses // Return all courses if search is empty
        }

        val lowerQuery = searchQuery.lowercase()

        // Simulate MongoDB Text Search (filtering based on name)
        return allCourses.filter { course ->
            course.name.lowercase().contains(lowerQuery)
        }
    }

    /**
     * FAKE REST implementation - simulates creating a new course via POST to /api/courses
     */
    suspend fun createCourse(courseDto: CourseCreateApiDto): CourseApiDto {
        delay(800) // Simulate network latency

        // Simulate successful ID generation by MongoDB
        val generatedId = "60e1f3b0c5d2a9f8e7d6b5aX" // Mock ID

        return CourseApiDto(
            id = generatedId,
            name = courseDto.name,
            location = courseDto.location,
            numberOfHoles = courseDto.numberOfHoles,
            parValues = courseDto.parValues,
            description = courseDto.description
        )
    }
}