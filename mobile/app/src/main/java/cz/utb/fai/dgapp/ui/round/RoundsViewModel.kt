package cz.utb.fai.dgapp.ui.round

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.utb.fai.dgapp.data.DefaultRoundRepository
import cz.utb.fai.dgapp.data.local.RoundLocalDataSource
import cz.utb.fai.dgapp.data.remote.RoundRemoteDataSource
import cz.utb.fai.dgapp.domain.Round
import cz.utb.fai.dgapp.domain.RoundRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoundsViewModel(private val repository: RoundRepository) : ViewModel() {
    // 1. Internal MutableStateFlow for Round List (History)
    private val _uiState = MutableStateFlow(RoundsUiState(isLoading = true))
    val uiState: StateFlow<RoundsUiState> = _uiState.asStateFlow()

    // 2. Internal MutableStateFlow for Round Detail
    private val _roundDetailState = MutableStateFlow(RoundDetailUiState())
    val roundDetailState: StateFlow<RoundDetailUiState> = _roundDetailState.asStateFlow()

    init {
        loadRounds(forceRefresh = false)
    }

    /**
     * Called by the UI after showing the success Snackbar to clear the state.
     */
    fun clearSaveStatus() {
        _uiState.update { it.copy(saveSuccessMessage = null) }
    }

    fun loadRounds(forceRefresh: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val rounds = repository.getRounds(forceRefresh)

                // Update state on success
                _uiState.update {
                    it.copy(
                        rounds = rounds,
                        isLoading = false
                    )
                }
            } catch (e: Exception){
                // Update state on failure
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to load rounds: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun getRound(roundId: String) {
        viewModelScope.launch {
            _roundDetailState.update { it.copy(isLoading = true, errorMessage = null, round = null) }

            try {
                val round = repository.getRoundById(roundId)

                _roundDetailState.update {
                    it.copy(
                        round = round,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _roundDetailState.update {
                    it.copy(
                        errorMessage = "Failed to load round details: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    // TODO
    fun saveNewRound() {}

    fun updateRound(round: Round) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccessMessage = null) }

            try {
                // 1. Call repository to update the round
                repository.updateRound(round)

                // 2. Update state on success: set the success message
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccessMessage = "Round updated successfully.",
                        errorMessage = null
                    )
                }

                // 3. Refresh the list view after successful update
                loadRounds(forceRefresh = true)

            } catch (e: Exception) {
                // 4. Update state on failure
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to update round: ${e.message}",
                        isSaving = false,
                        saveSuccessMessage = null
                    )
                }
            }
        }
    }

    fun deleteRound(roundId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccessMessage = null) }

            try {
                // 1. Call repository to delete the round
                repository.deleteRound(roundId)

                // Update state on success
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        saveSuccessMessage = "Round deleted successfully.",
                        errorMessage = null
                    )
                }
                // Refresh the list view after successful deletion
                loadRounds(forceRefresh = true)

            } catch (e: Exception) {
                // Update state on failure
                _uiState.update {
                    it.copy(
                        errorMessage = "Error while deleting the round: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    /*companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val remote = RoundRemoteDataSource()
                val local = RoundLocalDataSource()
                val repo = DefaultRoundRepository(remote, local)
                RoundsViewModel(repo)
            }
        }
    }*/

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // Mock dependencies for the factory (replace with real injection)
                val repository = DefaultRoundRepository(
                    RoundRemoteDataSource(),
                    // NOTE: localDataSource should be a proper Room implementation
                    RoundLocalDataSource()
                )
                return RoundsViewModel(repository) as T
            }
        }
    }
}