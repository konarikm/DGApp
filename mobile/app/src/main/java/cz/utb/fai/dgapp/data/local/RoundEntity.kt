package cz.utb.fai.dgapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Round Entity for Room database. Represents the 'rounds' table.
 * Uses Foreign Keys to link to PlayerEntity and CourseEntity.
 */
@Entity(
    tableName = "rounds",
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["player_id"],
            onDelete = ForeignKey.CASCADE // If player is deleted, delete the round
        ),
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["course_id"],
            onDelete = ForeignKey.NO_ACTION
        )
    ]
)
data class RoundEntity(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "player_id", index = true)
    val playerId: String,

    @ColumnInfo(name = "course_id", index = true)
    val courseId: String,

    @ColumnInfo(name = "scores_json")
    val scoresJson: String,

    val date: String
)