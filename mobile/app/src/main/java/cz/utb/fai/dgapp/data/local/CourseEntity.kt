package cz.utb.fai.dgapp.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Course Entity for Room database. Represents the 'courses' table.
 * Includes 'location' and 'description' as nullable fields to match the MongoDB schema.
 * Note: parValues are stored as a JSON string using TypeConverter.
 */
@Entity(tableName = "courses")
data class CourseEntity(
    @PrimaryKey
    val id: String, // Mapped from MongoDB _id
    val name: String,

    val location: String?,

    val description: String?,

    val numberOfHoles: Int,

    @ColumnInfo(name = "par_values_json")
    val parValuesJson: String
)
