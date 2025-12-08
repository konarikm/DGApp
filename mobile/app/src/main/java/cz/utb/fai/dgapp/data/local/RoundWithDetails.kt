package cz.utb.fai.dgapp.data.local

import androidx.room.Embedded
import androidx.room.Relation

data class RoundWithDetails(
    @Embedded
    val round: RoundEntity,

    @Relation(
        parentColumn = "playerId",
        entityColumn = "id"
    )
    val player: PlayerEntity,

    @Relation(
        parentColumn = "courseId",
        entityColumn = "id"
    )
    val course: CourseEntity
)
