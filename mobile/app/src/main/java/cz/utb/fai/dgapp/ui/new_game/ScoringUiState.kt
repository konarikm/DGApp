package cz.utb.fai.dgapp.ui.new_game

import cz.utb.fai.dgapp.domain.Course

data class ScoringUiState(
    val isLoading: Boolean = true,
    val course: Course? = null,
    val playerName: String = "",
    val currentHoleIndex: Int = 0,
    val scores: List<Int> = emptyList(), // Current scores for all holes
    val isRoundFinished: Boolean = false,
    val errorMessage: String? = null
)
