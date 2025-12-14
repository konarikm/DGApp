package cz.utb.fai.dgapp.ui.new_game

import cz.utb.fai.dgapp.domain.Course

data class NewGameUiState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(), // List of courses available for selection
    val errorMessage: String? = null,
    // State to trigger navigation to the Scoring Screen
    val isStartingRound: Boolean = false,
    val startedRoundId: String? = null
)
