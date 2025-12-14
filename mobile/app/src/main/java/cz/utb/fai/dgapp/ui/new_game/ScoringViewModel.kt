package cz.utb.fai.dgapp.ui.new_game

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.utb.fai.dgapp.data.DefaultRoundRepository
import cz.utb.fai.dgapp.data.DefaultCourseRepository
import cz.utb.fai.dgapp.data.local.AppDatabase
import cz.utb.fai.dgapp.data.local.CourseLocalDataSource
import cz.utb.fai.dgapp.data.local.RoundLocalDataSource
import cz.utb.fai.dgapp.data.remote.CourseRemoteDataSource
import cz.utb.fai.dgapp.data.remote.RoundRemoteDataSource
import cz.utb.fai.dgapp.domain.CourseRepository
import cz.utb.fai.dgapp.domain.Player
import cz.utb.fai.dgapp.domain.Round
import cz.utb.fai.dgapp.domain.RoundRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID

private val DEFAULT_SCORING_STATE = ScoringUiState(isLoading = true)

class ScoringViewModel(
    private val courseRepository: CourseRepository,
    private val roundRepository: RoundRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(DEFAULT_SCORING_STATE)
    val uiState: StateFlow<ScoringUiState> = _uiState.asStateFlow()

    private var currentPlayerId: String = "" // In real app, this comes from auth

    /**
     * Initializes the scoring session.
     * Must be called immediately after entering the screen.
     */
    fun initializeSession(courseId: String, playerName: String) {
        viewModelScope.launch {
            // Only reload and reset if the courseId is new or course is null (initial load)
            if (_uiState.value.course?.id == courseId && _uiState.value.course != null) {
                // Session is already initialized for this course, skip heavy reloading
                _uiState.update { it.copy(playerName = playerName) }
                return@launch
            }

            // Reset all scoring state before loading new data
            _uiState.update {
                ScoringUiState(isLoading = true)
            }

            try {
                // 1. Fetch Course Details (to get PARs)
                val course = courseRepository.getCourseById(courseId)

                // 2. Initialize scores with PAR values (standard behavior)
                // If parValues are missing, default to 3
                val initialScores = course.parValues.ifEmpty {
                    List(course.numberOfHoles) { 3 }
                }

                // 3. Setup State
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        course = course,
                        playerName = playerName,
                        scores = initialScores,
                        currentHoleIndex = 0,
                        isRoundFinished = false // Ensure round is not marked as finished
                    )
                }

                // TODO
                // Find player ID in DB / create new player
                // currentPlayerId = UUID.randomUUID().toString()

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    /**
     * Updates the score for the CURRENT hole.
     * @param delta +1 or -1
     */
    fun updateCurrentHoleScore(delta: Int) {
        val currentState = _uiState.value
        val currentScores = currentState.scores.toMutableList()
        val currentIndex = currentState.currentHoleIndex

        // Calculate new score (min 1)
        val newScore = (currentScores[currentIndex] + delta).coerceAtLeast(1)

        currentScores[currentIndex] = newScore

        _uiState.update { it.copy(scores = currentScores) }
    }

    fun nextHole() {
        val currentState = _uiState.value
        val maxIndex = (currentState.course?.numberOfHoles ?: 1) - 1

        if (currentState.currentHoleIndex < maxIndex) {
            _uiState.update { it.copy(currentHoleIndex = it.currentHoleIndex + 1) }
        }
    }

    fun previousHole() {
        val currentState = _uiState.value
        if (currentState.currentHoleIndex > 0) {
            _uiState.update { it.copy(currentHoleIndex = it.currentHoleIndex - 1) }
        }
    }

    /**
     * Saves the round to the database/API.
     */
    fun finishRound() {
        viewModelScope.launch {
            val state = _uiState.value
            if (state.course == null) return@launch

            _uiState.update { it.copy(isLoading = true) }

            try {
                // 1. Calculate Totals
                val totalPar = state.course.parValues.sum()
                val totalScore = state.scores.sum()

                // 2. Create Domain Object
                // val player = Player(currentPlayerId, state.playerName, null, null)

                // Now scoring for only 1 player
                val player = Player(id = "6918e4c190ea5c4759d49dd6", name = "Martin Konarik", pdgaNumber = 210450, email = "martink@email.com")

                val newRound = Round(
                    id = "", // Repo/API will assign ID
                    player = player,
                    course = state.course,
                    scores = state.scores,
                    date = LocalDate.now(),
                    totalPar = totalPar,
                    totalScore = totalScore,
                    parScore = totalScore - totalPar
                )

                roundRepository.createRound(newRound)

                _uiState.update { it.copy(isLoading = false, isRoundFinished = true) }

            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Failed to save round: ${e.message}") }
            }
        }
    }

    /**
     * Resets the entire ViewModel state to its initial, default configuration.
     * This is called when the user exits the scoring screen via back button or finishes a round.
     */
    fun resetSession() {
        _uiState.value = DEFAULT_SCORING_STATE
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                val db = AppDatabase.getDatabase(app.applicationContext)

                val courseRepo = DefaultCourseRepository(
                    CourseRemoteDataSource(),
                    CourseLocalDataSource(db.courseDao())
                )

                // Setup Round Repo
                val roundRemote = RoundRemoteDataSource()
                val roundLocal = RoundLocalDataSource(db.roundDao(), db.playerDao(), db.courseDao())
                val roundRepo = DefaultRoundRepository(roundRemote, roundLocal)

                ScoringViewModel(courseRepo, roundRepo)
            }
        }
    }
}