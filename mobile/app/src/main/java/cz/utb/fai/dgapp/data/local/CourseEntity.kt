package cz.utb.fai.dgapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val location: String?,
    val description: String?,
    val numberOfHoles: Int,

    @ColumnInfo(name = "par_values_json")
    val parValuesJson: String
)
