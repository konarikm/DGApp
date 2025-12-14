package cz.utb.fai.dgapp.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Retrofit interface defining the API endpoints for the Round resource.
 */
interface RoundApiService {

    /**
     * Retrieves a list of all rounds, populated with player and course details.
     * Endpoint: GET /api/rounds
     */
    @GET("api/rounds")
    suspend fun getRounds(): List<RoundApiDto>

    /**
     * Retrieves a single round by ID.
     * Endpoint: GET /api/rounds/{id}
     */
    @GET("api/rounds/{id}")
    suspend fun getRoundById(
        @Path("id") id: String
    ): RoundApiDto

    /**
     * Creates a new round.
     * Endpoint: POST /api/rounds
     */
    @POST("api/rounds")
    suspend fun createRound(
        @Body round: RoundCreateApiDto
    ): RoundCreationResponseDto

    /**
     * Updates an existing course.
     * Endpoint: PUT /api/rounds/{id}
     */
    @PUT("api/rounds/{id}")
    suspend fun updateRound(
        @Path("id") id: String,
        @Body round: RoundApiDto
    ): RoundUpdatedApiDto

    /**
     * Deletes a course by ID.
     * Endpoint: DELETE /api/rounds/{id}
     */
    @DELETE("api/rounds/{id}")
    suspend fun deleteRound(
        @Path("id") id: String
    )
    // We expect a 200/204 response with no body content on success.

}