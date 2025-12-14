package cz.utb.fai.dgapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.TypeConverter

/**
 * The main Room database class for the application.
 *
 * It is defined as abstract and uses the @Database annotation to list all entities
 * and DAOs.
 */
@Database(
    entities = [
        PlayerEntity::class,
        CourseEntity::class,
        RoundEntity::class
    ],
    version = 2, // Database version, must be incremented on schema changes
    exportSchema = false // Do not export schema to the filesystem
)
@TypeConverters(Converters::class) // Apply the Type Converters globally
abstract class AppDatabase : RoomDatabase() {

    // DAOs (Data Access Objects)
    abstract fun playerDao(): PlayerDao
    abstract fun courseDao(): CourseDao
    abstract fun roundDao(): RoundDao

    companion object {
        // Singleton pattern to prevent multiple instances
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "disc_golf_database"
                )
                    .fallbackToDestructiveMigration(false)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}