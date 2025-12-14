package cz.utb.fai.dgapp.data.remote

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

/**
 * Minimal DTO returned by the POST /api/rounds endpoint.
 * It contains only the ID of the newly created round.
 */
@Serializable
data class RoundCreationResponseDto(
    @SerialName("id")
    val id: String
)
