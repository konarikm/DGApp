package cz.utb.fai.dgapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseApiDto(
    @SerialName("_id")
    val id: String,
    val name: String,
    val location: String?,
    val description: String?,
    val numberOfHoles: Int,
    val parValues: List<Int>
)
