package cz.utb.fai.dgapp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class CourseCreateApiDto(
    val name: String,
    val location: String?,
    val numberOfHoles: Int,
    val description: String?,
    val parValues: List<Int>
)
