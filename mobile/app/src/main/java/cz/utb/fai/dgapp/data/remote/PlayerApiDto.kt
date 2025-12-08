package cz.utb.fai.dgapp.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PlayerApiDto(
    @SerialName("_id")
    val id: String,
    val name: String,
    val pdgaNumber: Int?,
    val email: String?
)

