package cz.utb.fai.dgapp.data.remote

import kotlinx.serialization.Serializable

/**
 * DTO used for creating a new Round (POST request).
 * It only contains foreign key IDs and scores.
 */
@Serializable
data class RoundCreateApiDto(
    val player: String,
    val course: String,
    val scores: List<Int>
)