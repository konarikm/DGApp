package cz.utb.fai.dgapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

/**
 * Data Access Object for the RoundEntity table, including complex joins (RoundWithDetails).
 */
@Dao
interface RoundDao {

    /**
     * Retrieves all rounds, joined with their Player and Course details.
     * Use @Transaction for queries that involve multiple tables (like relations/joins).
     */
    @Transaction
    @Query("SELECT * FROM rounds ORDER BY date DESC")
    suspend fun getAllRoundsWithDetails(): List<RoundWithDetails>

    /**
     * Retrieves a single round with details by ID.
     */
    @Transaction
    @Query("SELECT * FROM rounds WHERE id = :roundId")
    suspend fun getRoundWithDetailsById(roundId: String): RoundWithDetails?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRound(round: RoundEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(rounds: List<RoundEntity>)

    @Query("DELETE FROM rounds WHERE id = :roundId")
    suspend fun deleteRound(roundId: String)
}