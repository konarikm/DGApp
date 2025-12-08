package cz.utb.fai.dgapp.ui

import cz.utb.fai.dgapp.domain.Round

data class RoundsHistoryUiState(
    val isLoading: Boolean = false,
    val rounds: List<Round> = emptyList(),
    val errorMessage: String? = null
)
