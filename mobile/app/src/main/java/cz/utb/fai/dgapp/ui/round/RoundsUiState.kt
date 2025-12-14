package cz.utb.fai.dgapp.ui.round

import cz.utb.fai.dgapp.domain.Round

data class RoundsUiState(
    val isLoading: Boolean = false,
    val rounds: List<Round> = emptyList(),
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val saveSuccessMessage: String? = null
)