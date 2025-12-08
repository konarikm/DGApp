package cz.utb.fai.dgapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoundApiDto(
    @SerialName("_id")
    val id: String,
    val player: PlayerApiDto,
    val course: CourseApiDto,
    val date: String,
    val scores: List<Int>
)
