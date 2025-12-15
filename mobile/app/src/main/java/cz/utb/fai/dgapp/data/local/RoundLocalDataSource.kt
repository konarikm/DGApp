package cz.utb.fai.dgapp.data.local

import cz.utb.fai.dgapp.data.mappers.toEntity
import cz.utb.fai.dgapp.domain.Round
import cz.utb.fai.dgapp.data.mappers.toEntity as toEntityCourse
import cz.utb.fai.dgapp.data.mappers.toEntity as toEntityPlayer

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
     * Accepts the full Domain model list to access nested entities.
     * This is used after fetching data from the API (remote refresh).
     */
    suspend fun saveRounds(rounds: List<Round>) {
        // 1. Extract and save all unique Player and Course entities first
        val playerEntities = rounds.map { it.player.toEntityPlayer() }.distinctBy { it.id }
        val courseEntities = rounds.map { it.course.toEntityCourse() }.distinctBy { it.id }

        // Use REPLACE strategy defined in DAO to update existing ones and insert new ones
        playerDao.insertAll(playerEntities)
        courseDao.insertAll(courseEntities)

        // 2. Map rounds to RoundEntity and save them
        val roundEntities = rounds.map { it.toEntity() }
        roundDao.insertAll(roundEntities)
    }

    /**
     * Saves a single round and its nested entities to the database.
     * This method assumes it receives the Domain model 'Round' from the repository
     * which contains the full nested Player and Course objects.
     */
    suspend fun saveRound(round: Round) {
        playerDao.insertPlayer(round.player.toEntityPlayer())
        courseDao.insertCourse(round.course.toEntityCourse())

        // 2. Then save the round itself
        roundDao.insertRound(round.toEntity())
    }

    /**
     * Saves a single round to the database.
     */
    suspend fun saveRound(round: RoundEntity) {
        // Delegates directly to the Room DAO
        roundDao.insertRound(round)
    }

    /**
     * Deletes a round by ID from the Room database.
     */
    suspend fun deleteRound(id: String) {
        roundDao.deleteRound(id)
        // Player/Course entities are generally kept, as they may be referenced by other rounds.
    }
}