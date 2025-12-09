package cz.utb.fai.dgapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.utb.fai.dgapp.data.DefaultRoundRepository
import cz.utb.fai.dgapp.data.local.RoundLocalDataSource
import cz.utb.fai.dgapp.data.remote.RoundRemoteDataSource
import cz.utb.fai.dgapp.domain.RoundRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RoundsHistoryViewModel(private val repository: RoundRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(RoundsHistoryUiState(isLoading = true))

    val uiState: StateFlow<RoundsHistoryUiState> = _uiState.asStateFlow()

    init {
        loadRounds(forceRefresh = false)
    }

    fun loadRounds(forceRefresh: Boolean) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val rounds = repository.getRounds(forceRefresh)

                // Update state on success
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rounds = rounds
                    )
                }
            } catch (e: Exception){
                // Update state on failure
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        rounds = emptyList(),
                        errorMessage = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val remote = RoundRemoteDataSource()
                val local = RoundLocalDataSource()
                val repo = DefaultRoundRepository(remote, local)
                RoundsHistoryViewModel(repo)
            }
        }
    }
}