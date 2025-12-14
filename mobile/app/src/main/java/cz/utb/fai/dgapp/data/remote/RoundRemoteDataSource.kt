package cz.utb.fai.dgapp.data.remote

import androidx.compose.ui.tooling.data.R
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory

// Retrofit Setup
private const val BASE_URL = "https://dgapp-api.onrender.com/"
private val json = Json { ignoreUnknownKeys = true }

/**
 * Creates the Retrofit instance and the RoundApiService implementation.
 */
private val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

private val roundApiService: RoundApiService = retrofit.create(RoundApiService::class.java)

/**
 * Actual implementation of the remote data source using Retrofit.
 * Handles fetching Round data from the deployed API.
 */
class RoundRemoteDataSource{

    /**
     * Implements GET ALL rounds from the API.
     */
    suspend fun getRounds(): List<RoundApiDto> {
        return roundApiService.getRounds()
    }

    /**
     * Implements GET by ID from the API.
     */
    suspend fun getRoundById(id: String): RoundApiDto {
        return roundApiService.getRoundById(id)
    }

    /**
     * Implements UPDATE (PUT) via the API.
     */
    suspend fun updateRound(roundDto: RoundApiDto): RoundUpdatedApiDto {
        return roundApiService.updateRound(roundDto.id, roundDto)
    }

    /**
     * Implements DELETE via the API.
     */
    suspend fun deleteRound(id: String) {
        roundApiService.deleteRound(id)
    }
}