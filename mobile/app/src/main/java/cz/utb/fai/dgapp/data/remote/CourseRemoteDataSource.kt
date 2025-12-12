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

    /**
     * FAKE REST implementation - simulates fetching a course by ID.
     */
    suspend fun getCourseById(id: String): CourseApiDto {
        delay(300) // Simulate network latency

        return when (id) {
            MOCK_COURSE_LAGUNA.id -> MOCK_COURSE_LAGUNA
            MOCK_COURSE_TUCIN.id -> MOCK_COURSE_TUCIN
            else -> throw NoSuchElementException("Course with ID $id not found on remote server.")
        }
    }

    /**
     * FAKE REST implementation - simulates updating an existing course (PUT)
     * Takes the full CourseDetailApiDto which MUST include the ID.
     */
    suspend fun updateCourse(courseDto: CourseApiDto): CourseApiDto {
        delay(800) // Simulate network latency

        // Simulate successful update (returns the received DTO, confirming the change)
        println("Simulating PUT request for course ID: ${courseDto.id}")
        return courseDto
    }

    /**
     * FAKE REST implementation - simulates deleting a course by ID.
     */
    suspend fun deleteCourse(id: String) {
        delay(500) // Simulate network latency

        // Mock check to simulate server success (no content returned)
        if (id == "non-existent-id") {
            throw NoSuchElementException("Course with ID $id not found for deletion.")
        }
        println("Simulating DELETE request for course ID: $id")
    }
}