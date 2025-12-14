package cz.utb.fai.dgapp.ui.new_game

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.utb.fai.dgapp.data.DefaultCourseRepository
import cz.utb.fai.dgapp.data.local.AppDatabase
import cz.utb.fai.dgapp.data.local.CourseLocalDataSource
import cz.utb.fai.dgapp.data.remote.CourseRemoteDataSource
import cz.utb.fai.dgapp.domain.CourseRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewGameViewModel(
    private val courseRepository: CourseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewGameUiState(isLoading = true))
    val uiState: StateFlow<NewGameUiState> = _uiState.asStateFlow()

    init {
        loadCourses()
    }

    /**
     * Loads the list of courses from the repository (will fetch from local cache or API).
     */
    fun loadCourses() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Fetch all courses (empty search query)
                val courses = courseRepository.getCourses(searchQuery = "")

                _uiState.update { it.copy(
                    isLoading = false,
                    courses = courses
                ) }
            } catch (e: Exception) {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "Failed to load courses: ${e.message}"
                ) }
            }
        }
    }

    /**
     * Initiates the round start process.
     * In a full application, this would create Player/Round entities in the DB.
     */
    fun startRound(formState: NewGameFormState) {
        viewModelScope.launch {
            _uiState.update { it.copy(isStartingRound = true) }

            // 1. Placeholder for Player creation (or finding existing player)
            println("Starting round for Player: ${formState.playerName} on Course ID: ${formState.selectedCourseId}")

            // 2. SIMULATION: Create a unique ID for the scoring session.
            // This ID will be used to navigate to the ScoringScreen.

            _uiState.update { it.copy(
                isStartingRound = false,
                // Placeholder ID for the started round
                startedRoundId = "new_round_${System.currentTimeMillis()}"
            ) }

            // In a real app, we would clear startedRoundId after navigation
        }
    }

    /**
     * Clears the startedRoundId status after navigation is initiated.
     * CRITICAL: Prevents subsequent attempts to start a round from failing.
     */
    fun clearStartStatus() {
        _uiState.update { it.copy(startedRoundId = null) }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Get the application context from the AndroidViewModelFactory context
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application

                // Initialize the Room database singleton
                val database = AppDatabase.getDatabase(application.applicationContext)

                // Instantiate the local data source with the Room DAO
                val localDataSource = CourseLocalDataSource(database.courseDao())

                val remoteDataSource = CourseRemoteDataSource()

                val repository = DefaultCourseRepository(
                    remoteDataSource = remoteDataSource,
                    localDataSource = localDataSource // Use the Room-backed data source
                )
                NewGameViewModel(courseRepository = repository)
            }
        }
    }

}