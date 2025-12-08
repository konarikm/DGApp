package cz.utb.fai.dgapp.data

import cz.utb.fai.dgapp.data.local.RoundLocalDataSource
import cz.utb.fai.dgapp.data.remote.RoundRemoteDataSource
import cz.utb.fai.dgapp.domain.RoundRepository
import cz.utb.fai.dgapp.data.mappers.toDomain
import cz.utb.fai.dgapp.data.mappers.toEntity
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

            /*val entities = domainRounds.map{ it.toEntity() }
            localDataSource.saveRounds(entities)*/

            domainRounds
        } else {
            local.map{ it.toDomain() }
        }
    }
}
