package cz.utb.fai.dgapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Data Access Object for the PlayerEntity table.
 */
@Dao
interface PlayerDao {

    @Query("SELECT * FROM players WHERE id = :playerId")
    suspend fun getPlayerById(playerId: String): PlayerEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlayer(player: PlayerEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(players: List<PlayerEntity>)

    @Query("DELETE FROM players WHERE id = :playerId")
    suspend fun deletePlayer(playerId: String)
}