package cz.utb.fai.dgapp.data.local

import kotlinx.coroutines.delay

/**
 * Fake "Room" datasource – drží data jen v paměti.
 */

// TODO
// implementovat reálnou instanci Room databáze

class RoundLocalDataSource{
    private var cache: List<RoundWithDetails> = emptyList()

    suspend fun getRounds(): List<RoundWithDetails> {
        delay(200)
        return cache
    }

    suspend fun saveRounds(rounds: List<RoundWithDetails>) {
        delay(200)
        cache = rounds
    }
}
