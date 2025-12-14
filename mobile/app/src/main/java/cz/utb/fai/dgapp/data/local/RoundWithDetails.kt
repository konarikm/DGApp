package cz.utb.fai.dgapp.data.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Data class representing a Round joined with its related Player and Course details.
 * This structure is used by the Room DAO queries to return a fully populated Round record.
 */
data class RoundWithDetails(
    @Embedded
    val round: RoundEntity,

    @Relation(
        parentColumn = "player_id",
        entityColumn = "id"
    )
    val player: PlayerEntity,

    @Relation(
        parentColumn = "course_id",
        entityColumn = "id"
    )
    val course: CourseEntity
)
