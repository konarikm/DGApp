package cz.utb.fai.dgapp.domain

data class Player (
    val id: String,
    val name: String,
    val pdgaNumber: Int?,
    val email: String?
)