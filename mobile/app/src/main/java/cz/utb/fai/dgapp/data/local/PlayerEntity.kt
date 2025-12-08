package cz.utb.fai.dgapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "players")
data class PlayerEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val pdgaNumber: Int?,
    val email: String?
)
