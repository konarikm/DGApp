package cz.utb.fai.dgapp.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import cz.utb.fai.dgapp.data.DefaultCourseRepository
import cz.utb.fai.dgapp.data.local.CourseLocalDataSource
import cz.utb.fai.dgapp.data.remote.CourseRemoteDataSource
import cz.utb.fai.dgapp.domain.CourseRepository
import kotlinx.coroutines.launch

class CoursesViewModel(private val repository: CourseRepository) : ViewModel() {
    var uiState by mutableStateOf(CoursesUiState(isLoading = true))
        private set

    init {
        loadCourses(forceRefresh = false)
    }

    fun loadCourses(forceRefresh: Boolean) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)

            try {
                val courses = repository.getCourses(forceRefresh)
                uiState = CoursesUiState(
                    isLoading = false,
                    courses = courses,
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState = CoursesUiState(
                    isLoading = false,
                    courses = emptyList(),
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val remote = CourseRemoteDataSource()
                val local = CourseLocalDataSource()
                val repo = DefaultCourseRepository(remote, local)
                CoursesViewModel(repo)
            }
        }
    }
}