package cz.utb.fai.dgapp.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class RoundCreateApiDto (
    val playerId: String,
    val courseId: String,
    val scores: List<Int>,
    val date: String
)