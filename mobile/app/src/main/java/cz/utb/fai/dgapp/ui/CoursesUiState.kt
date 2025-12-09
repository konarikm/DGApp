package cz.utb.fai.dgapp.ui

import cz.utb.fai.dgapp.domain.Course

data class CoursesUiState(
    val isLoading: Boolean = false,
    val courses: List<Course> = emptyList(),
    val errorMessage: String? = null,
    val searchQuery: String = ""
)
