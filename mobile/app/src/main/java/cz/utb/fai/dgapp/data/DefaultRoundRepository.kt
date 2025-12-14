package cz.utb.fai.dgapp.data

import cz.utb.fai.dgapp.data.local.RoundLocalDataSource
import cz.utb.fai.dgapp.data.mappers.toApiDto
import cz.utb.fai.dgapp.data.remote.RoundRemoteDataSource
import cz.utb.fai.dgapp.domain.RoundRepository
import cz.utb.fai.dgapp.data.mappers.toDomain
import cz.utb.fai.dgapp.domain.Round

class DefaultRoundRepository(
    private val remoteDataSource: RoundRemoteDataSource,
    private val localDataSource: RoundLocalDataSource,
) : RoundRepository {
    override suspend fun getRounds(forceRefresh: Boolean): List<Round> {
        val local = localDataSource.getRounds()
        val shouldRefresh = forceRefresh || local.isEmpty()

        return if (shouldRefresh) {
            val remoteDtos = remoteDataSource.getRounds()
            val domainRounds = remoteDtos.map{ it.toDomain() }

            localDataSource.saveRounds(domainRounds)

            domainRounds
        } else {
            local.map{ it.toDomain() }
        }
    }

    override suspend fun getRoundById(id: String): Round {
        // 1. Try to get from local cache
        val localEntity = localDataSource.getRoundById(id)

        return if (localEntity != null) {
            // Found in cache, convert and return
            localEntity.toDomain()
        } else {
            // 2. Not found locally, fetch from remote
            val remoteDto = remoteDataSource.getRoundById(id)

            // 3. Convert DTO to Domain
            val domainRound = remoteDto.toDomain()

            // 4. Cache the new data
            localDataSource.saveRound(domainRound)

            return domainRound
        }
    }

    override suspend fun updateRound(round: Round): Round {
        // 1. Convert Domain model to API DTO
        // Since it's an update, the DTO must contain the ID.
        val apiDto = round.toApiDto()

        // 2. Call remote API to update the round (PUT request)
        // Retrofit returns RoundUpdatedApiDto (just ID).
        // We don't need to capture the response, only confirm success.
        val updatedRoundDto = remoteDataSource.updateRound(apiDto)

        // 3. Since the API only returns ID, we update the local cache using the
        // original domain model, which now contains the updated scores/date.
        localDataSource.saveRound(round)

        // 4. Return the updated domain model itself.
        return round
    }

    override suspend fun deleteRound(id: String) {
        // 1. Call remote API to delete the round
        remoteDataSource.deleteRound(id)

        // 2. Remove from local cache
        localDataSource.deleteRound(id)
    }

}
