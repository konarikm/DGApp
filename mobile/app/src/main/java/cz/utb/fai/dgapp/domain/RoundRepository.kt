package cz.utb.fai.dgapp.domain

interface RoundRepository {
    suspend fun getRounds(forceRefresh: Boolean = false): List<Round>
}