package cz.utb.fai.dgapp.data.local

import cz.utb.fai.dgapp.domain.Round
import cz.utb.fai.dgapp.data.mappers.toEntity as toEntityCourse
import cz.utb.fai.dgapp.data.mappers.toEntity as toEntityPlayer
import cz.utb.fai.dgapp.data.mappers.toEntity as toEntityRound

/**
 * Local datasource implementation using Room database (RoundDao).
 */
class RoundLocalDataSource(
    // Inject all necessary DAOs
    private val roundDao: RoundDao,
    private val playerDao: PlayerDao,
    private val courseDao: CourseDao
) {

    /**
     * Retrieves all rounds, joined with details, from the Room database.
     */
    suspend fun getRounds(): List<RoundWithDetails> {
        return roundDao.getAllRoundsWithDetails()
    }

    /**
     * Retrieves a specific round with details by ID from the Room database.
     */
    suspend fun getRoundById(id: String): RoundWithDetails? {
        return roundDao.getRoundWithDetailsById(id)
    }

    /**
     * Saves a list of rounds and their nested player/course entities to the database.
     */
    suspend fun saveRounds(rounds: List<Round>) {
        rounds.forEach { round ->
            // We must save the nested entities (Player/Course) first
            playerDao.insertPlayer(round.player.toEntityPlayer())
            courseDao.insertCourse(round.course.toEntityCourse())

            // Then save the round itself
            roundDao.insertRound(round.toEntityRound())
        }
    }

    /**
     * Saves a single round and its nested entities to the database.
     */
    suspend fun saveRound(round: Round) {
        // Save player and course entities first
        playerDao.insertPlayer(round.player.toEntityPlayer())
        courseDao.insertCourse(round.course.toEntityCourse())

        // Then save the round itself
        roundDao.insertRound(round.toEntityRound())
    }

    /**
     * Deletes a round by ID from the Room database.
     */
    suspend fun deleteRound(id: String) {
        roundDao.deleteRound(id)
        // Player/Course entities are generally kept, as they may be referenced by other rounds.
    }
}