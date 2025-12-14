package cz.utb.fai.dgapp.ui.course

import cz.utb.fai.dgapp.domain.Course

data class CourseDetailUiState(
    val isLoading: Boolean = false,
    val course: Course? = null,
    val errorMessage: String? = null
)