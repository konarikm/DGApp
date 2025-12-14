package cz.utb.fai.dgapp.data.local

import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap

/**
 * Fake "Room" datasource – drží data jen v paměti.
 * NOTE: The implementation must now return the joined structure (RoundWithDetails)
 * to simulate the Room DAO behavior.
 */

// Global cache for simpler entities (used internally for building RoundWithDetails)
private val roundCache: ConcurrentHashMap<String, RoundEntity> = ConcurrentHashMap()
private val playerCache: ConcurrentHashMap<String, PlayerEntity> = ConcurrentHashMap()
private val courseCache: ConcurrentHashMap<String, CourseEntity> = ConcurrentHashMap()

class RoundLocalDataSource{
    // Initialize some mock data for the detail view to work
    init {
        val converters = Converters()
        val player = PlayerEntity("p1", "Mock Player", 100, "mock@example.com")
        val course = CourseEntity(
            "c1", "Mock Course (9)", "Mock Location", null, 9,
            converters.fromIntList(listOf(3, 3, 3, 3, 3, 3, 3, 3, 3))
        )
        val round = RoundEntity("r1", player.id, course.id, converters.fromIntList(listOf(3, 4, 3, 3, 3, 3, 3, 3, 3)), "2025-12-13")

        playerCache[player.id] = player
        courseCache[course.id] = course
        roundCache[round.id] = round
    }

    // Return the joined structure
    suspend fun getRounds(): List<RoundWithDetails> {
        delay(200)
        // In a real app, this would be a single Room query returning List<RoundWithDetails>
        return roundCache.values.mapNotNull { roundEntity ->
            val playerEntity = playerCache[roundEntity.playerId]
            val courseEntity = courseCache[roundEntity.courseId]
            if (playerEntity != null && courseEntity != null) {
                RoundWithDetails(roundEntity, playerEntity, courseEntity)
            } else null
        }
    }

    /**
     * Returns a single RoundWithDetails object for detail view.
     */
    suspend fun getRoundById(id: String): RoundWithDetails? {
        delay(100)
        // In a real app, this would be a single Room query returning RoundWithDetails?
        val roundEntity = roundCache[id]
        val playerEntity = roundEntity?.let { playerCache[it.playerId] }
        val courseEntity = roundEntity?.let { courseCache[it.courseId] }

        return if (roundEntity != null && playerEntity != null && courseEntity != null) {
            RoundWithDetails(roundEntity, playerEntity, courseEntity)
        } else null
    }

    // Input must accept the list of entities for caching
    // Note: In a real app, saving would require multiple DAO calls (playerDao.insert, courseDao.insert, roundDao.insert)
    suspend fun saveRounds(rounds: List<RoundEntity>) {
        delay(200)
        rounds.forEach { roundCache[it.id] = it }
    }

    suspend fun saveRound(round: RoundEntity) {
        delay(100)
        roundCache[round.id] = round
    }

    suspend fun deleteRound(id: String) {
        delay(100)
        roundCache.remove(id)
    }
}