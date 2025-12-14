package cz.utb.fai.dgapp.domain

interface RoundRepository {
    /**
     * Retrieves rounds, either from local cache or remote API.
     *
     * @param forceRefresh If true, skips local data and fetches from remote.
     * @return List of matching Round domain models.
     */
    suspend fun getRounds(forceRefresh: Boolean = false): List<Round>

    /**
     * Retrieves a single Round by its ID.
     */
    suspend fun getRoundById(id: String): Round

    /**
     * Updates an existing Round on the remote API and updates the local cache. (PUT)
     */
    suspend fun updateRound(round: Round): Round

    /**
     * Deletes a Round by ID from the remote API and local cache. (DELETE)
     */
    suspend fun deleteRound(id: String)
}