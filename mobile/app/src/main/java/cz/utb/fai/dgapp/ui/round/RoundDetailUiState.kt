package cz.utb.fai.dgapp.ui.round

import cz.utb.fai.dgapp.domain.Round

data class RoundDetailUiState(
    val isLoading: Boolean = false,
    val round: Round? = null,
    val errorMessage: String? = null
)