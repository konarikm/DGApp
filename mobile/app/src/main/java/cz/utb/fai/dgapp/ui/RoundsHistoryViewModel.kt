package cz.utb.fai.dgapp.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.utb.fai.dgapp.data.DefaultRoundRepository
import cz.utb.fai.dgapp.data.local.RoundLocalDataSource
import cz.utb.fai.dgapp.data.remote.RoundRemoteDataSource
import cz.utb.fai.dgapp.domain.RoundRepository
import kotlinx.coroutines.launch

class RoundsHistoryViewModel(private val repository: RoundRepository) : ViewModel() {
    var uiState by mutableStateOf(RoundsHistoryUiState(isLoading = true))
        private set

    init {
        loadRounds(forceRefresh = false)
    }

    fun loadRounds(forceRefresh: Boolean) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val rounds = repository.getRounds(forceRefresh)
                uiState = RoundsHistoryUiState(
                    isLoading = false,
                    rounds = rounds,
                    errorMessage = null
                )
            } catch (e: Exception){
                uiState = RoundsHistoryUiState(
                    isLoading = false,
                    rounds = emptyList(),
                    errorMessage = e.message ?: "Unknown error"
                )
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