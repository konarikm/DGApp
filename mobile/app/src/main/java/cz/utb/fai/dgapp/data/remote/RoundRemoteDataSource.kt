package cz.utb.fai.dgapp.data.remote

import kotlinx.coroutines.delay

class RoundRemoteDataSource{

    // Define mock data constants for easy readability
    private val MOCK_PLAYER_MARTIN = PlayerApiDto(
        id = "6918e4c190ea5c4759d49dd6",
        name = "Martin Konarik",
        pdgaNumber = 210450,
        email = "martink@email.com"
    )

    private val MOCK_PLAYER_PETR = PlayerApiDto(
        id = "69331232f43ad2a8a0dfb9ba",
        name = "Petr Svoboda",
        pdgaNumber = null,
        email = "petr.svoboda@example.com"
    )

    private val MOCK_COURSE_LAGUNA = CourseApiDto(
        id = "6918e1e390ea5c4759d49dcc",
        name = "DiscgolfPark Laguna Přerov",
        location = "Přerov, Czechia",
        description = null,
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
    suspend fun getRounds(): List<RoundApiDto> {
        delay(1000)

        return listOf(
            RoundApiDto(
                id = "1",
                player = MOCK_PLAYER_MARTIN,
                course = MOCK_COURSE_LAGUNA,
                scores = listOf(2, 2, 2, 2, 2, 2, 2, 2, 2),
                date = "2025-01-01",
            ),

            RoundApiDto(
                id = "2",
                player = MOCK_PLAYER_PETR,
                course = MOCK_COURSE_LAGUNA,
                scores = listOf(3, 4, 3, 5, 3, 3, 3, 3, 3),
                date = "2025-10-12"
            ),

            RoundApiDto(
                id = "3",
                player = MOCK_PLAYER_MARTIN,
                course = MOCK_COURSE_TUCIN,
                scores = listOf(3, 3, 3, 3, 3, 3, 3, 3, 3, 2),
                date = "2025-07-23"
            ),
        )
    }
}
