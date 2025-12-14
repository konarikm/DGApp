package cz.utb.fai.dgapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RoundUpdatedApiDto(
    @SerialName("_id")
    val id: String
)
