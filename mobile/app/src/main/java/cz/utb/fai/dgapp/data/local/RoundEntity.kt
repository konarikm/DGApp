package cz.utb.fai.dgapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "rounds",
    foreignKeys = [
        ForeignKey(
            entity = PlayerEntity::class,
            parentColumns = ["id"],
            childColumns = ["playerId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CourseEntity::class,
            parentColumns = ["id"],
            childColumns = ["courseId"],
            onDelete = ForeignKey.RESTRICT
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
