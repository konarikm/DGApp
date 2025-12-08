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

    // FAKE REST - simulace síťového volání
    suspend fun getCourses(): List<CourseApiDto> {
        delay(1000)

        return listOf(
            MOCK_COURSE_LAGUNA,
            MOCK_COURSE_TUCIN,
        )
    }
}