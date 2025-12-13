package cz.utb.fai.dgapp.data.remote

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit interface defining the API endpoints for the Course resource.
 */
interface CourseApiService {

    /**
     * Retrieves a list of courses. Supports optional text search.
     * Endpoint: GET /api/courses?search={query}
     */
    @GET("api/courses")
    suspend fun getCourses(
        @Query("search") query: String?
    ): List<CourseApiDto>

    /**
     * Retrieves a single course by ID.
     * Endpoint: GET /api/courses/{id}
     */
    @GET("api/courses/{id}")
    suspend fun getCourseById(
        @Path("id") id: String
    ): CourseApiDto


    /**
     * Creates a new course.
     * Endpoint: POST /api/courses
     */
    @POST("api/courses")
    suspend fun createCourse(
        @Body course: CourseCreateApiDto
    ): CourseApiDto // Returns the created course with its new ID

    /**
     * Updates an existing course.
     * Endpoint: PUT /api/courses/{id}
     */
    @PUT("api/courses/{id}")
    suspend fun updateCourse(
        @Path("id") id: String,
        @Body course: CourseApiDto // Requires full DTO with updated fields
    ): CourseApiDto // Returns the updated course

    /**
     * Deletes a course by ID.
     * Endpoint: DELETE /api/courses/{id}
     */
    @DELETE("api/courses/{id}")
    suspend fun deleteCourse(
        @Path("id") id: String
    )
    // We expect a 200/204 response with no body content on success.
}