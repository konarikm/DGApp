package cz.utb.fai.dgapp.ui.course

import cz.utb.fai.dgapp.domain.Course

data class CoursesUiState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val isSaving: Boolean = false,
    val saveSuccessMessage: String? = null,
)