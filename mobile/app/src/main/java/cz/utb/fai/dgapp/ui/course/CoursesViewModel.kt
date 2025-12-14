package cz.utb.fai.dgapp.ui.course

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
import cz.utb.fai.dgapp.data.remote.RoundRemoteDataSource
import cz.utb.fai.dgapp.domain.Course
import cz.utb.fai.dgapp.domain.CourseRepository
import cz.utb.fai.dgapp.ui.course.NewCourseFormState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(FlowPreview::class)
class CoursesViewModel(private val repository: CourseRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _uiState = MutableStateFlow(CoursesUiState())
    val uiState: StateFlow<CoursesUiState> = _uiState.asStateFlow()

    private val _courseDetailState = MutableStateFlow(CourseDetailUiState())
    val courseDetailState: StateFlow<CourseDetailUiState> = _courseDetailState.asStateFlow()

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

    /**
     * Called by the UI after showing the success Snackbar to clear the state.
     */
    fun clearSaveStatus() {
        _uiState.update { it.copy(saveSuccessMessage = null) }
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

    fun getCourse(courseId: String) {
        viewModelScope.launch {
            _courseDetailState.update { it.copy(isLoading = true, errorMessage = null, course = null) }

            try {
                val course = repository.getCourseById(courseId)

                _courseDetailState.update {
                    it.copy(
                        course = course,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _courseDetailState.update {
                    it.copy(
                        errorMessage = "Failed to load course details: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun saveNewCourse(formState: NewCourseFormState) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccessMessage = null) }

            // 1. Create the Domain Model from the Form State
            val newCourse = Course(
                // ID is empty, server will generate it
                id = "",
                name = formState.name,
                location = formState.location,
                description = formState.description,
                numberOfHoles = formState.numberOfHoles,
                // Generate default Par 3 values
                parValues = formState.defaultParValues
            )

            try {
                // 2. Call repository to create the course
                repository.createCourse(newCourse)

                // 3. Update state on success and trigger a list refresh
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccessMessage = "Course '${newCourse.name}' was created successfully.",
                        errorMessage = null
                    )
                }
                // Refresh the list view after successful save
                loadCourses(searchQuery.value, forceRefresh = true)

            } catch (e: Exception) {
                // 4. Update state on failure
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to save course: ${e.message}",
                        isSaving = false,
                        saveSuccessMessage = null
                    )
                }
            }
        }
    }

    fun updateCourse(course: Course) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null, saveSuccessMessage = null) }

            try {
                // 1. Call repository to update the course
                repository.updateCourse(course)

                // 2. Update state on success: set the success message
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        saveSuccessMessage = "Course updated successfully.",
                        errorMessage = null
                    )
                }

                // 3. Refresh the list view after successful update
                loadCourses(_searchQuery.value, forceRefresh = true)

            } catch (e: Exception) {
                // 4. Update state on failure
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to update course: ${e.message}",
                        isSaving = false,
                        saveSuccessMessage = null
                    )
                }
            }
        }
    }

    fun deleteCourse(courseId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, saveSuccessMessage = null) }

            try {
                // 1. Call repository to delete the course
                repository.deleteCourse(courseId)

                // 2. Update state on success
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        saveSuccessMessage = "Course deleted successfully.",
                        errorMessage = null
                    )
                }
                // 3. Refresh the list view after successful deletion
                loadCourses(_searchQuery.value, forceRefresh = true)

            } catch (e: Exception) {
                // 4. Update state on failure
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to delete course: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Get the application context from the AndroidViewModelFactory context
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application

                // Initialize the Room database singleton
                val database = AppDatabase.getDatabase(application.applicationContext)

                // Instantiate the data source with the Room DAO
                val localDataSource = CourseLocalDataSource(database.courseDao())

                // Instantiate the remote data source
                val remoteDataSource = CourseRemoteDataSource()

                val repository = DefaultCourseRepository(
                    remoteDataSource = remoteDataSource,
                    localDataSource = localDataSource // Use the Room-backed data source
                )
                CoursesViewModel(repository)
            }
        }
    }
}