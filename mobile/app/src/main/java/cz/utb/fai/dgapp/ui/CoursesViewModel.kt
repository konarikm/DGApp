package cz.utb.fai.dgapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import cz.utb.fai.dgapp.data.DefaultCourseRepository
import cz.utb.fai.dgapp.data.local.CourseLocalDataSource
import cz.utb.fai.dgapp.data.remote.CourseRemoteDataSource
import cz.utb.fai.dgapp.domain.CourseRepository
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class CoursesViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(CoursesUiState())
    val uiState: StateFlow<CoursesUiState> = _uiState.asStateFlow()

    init {
        // This pipeline handles both initial load and search queries
        searchQuery
            .debounce(300L) // Wait 300ms after the user stops typing
            .distinctUntilChanged() // Only proceed if the query actually changed
            .onEach { query -> loadCourses(query) }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChange(newQuery: String) {
        // Update the state immediately for the UI to reflect the text change
        _uiState.update { it.copy(searchQuery = newQuery) }
        _searchQuery.value = newQuery // Triggers the debounce pipeline
    }

    fun refreshCourses() {
        // Force refresh only if the search box is empty (search handled by pipeline)
        if (searchQuery.value.isBlank()) {
            loadCourses(searchQuery.value, forceRefresh = true)
        }
    }

    private fun loadCourses(query: String, forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Repository handles whether to search remote or load cache
                val courses = repository.getCourses(query, forceRefresh)

                _uiState.update {
                    it.copy(
                        courses = courses,
                        isLoading = false,
                        // Ensure searchQuery in UI state reflects the active query
                        searchQuery = query
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to load courses: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    // --- Factory (needed for integration with Compose) ---

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                // Mock dependencies for the factory (replace with real injection)
                val repository = DefaultCourseRepository(
                    CourseRemoteDataSource(),
                    // NOTE: localDataSource should be a proper Room implementation
                    CourseLocalDataSource()
                )
                return CoursesViewModel(repository) as T
            }
        }
    }
}