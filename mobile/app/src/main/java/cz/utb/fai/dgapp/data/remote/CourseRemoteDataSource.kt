package cz.utb.fai.dgapp.data.remote


import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.text.Normalizer

// --- Retrofit Setup ---
private const val BASE_URL = "https://dgapp-api.onrender.com/"
private val json = Json { ignoreUnknownKeys = true }

/**
 * Creates the Retrofit instance and the CourseApiService implementation.
 */
private val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

private val courseApiService: CourseApiService = retrofit.create(CourseApiService::class.java)

/**
 * Actual implementation of the remote data source using Retrofit.
 */
class CourseRemoteDataSource{

    // Utility function to remove accents and normalize text (retained for search logic consistency) ---
    private fun String.stripAccents(): String {
        val normalized = Normalizer.normalize(this, Normalizer.Form.NFD)
        return normalized.replace("\\p{Mn}+".toRegex(), "")
    }

    /**
     * Implements GET ALL or GET by SEARCH from the API.
     */
    suspend fun getCourses(searchQuery: String): List<CourseApiDto> {
        // If query is provided, send it; otherwise, send null to get all.
        val apiQuery = if (searchQuery.isNotBlank()) searchQuery.stripAccents() else null

        // Use the Retrofit service to fetch data
        return courseApiService.getCourses(apiQuery)
    }

    /**
     * Implements GET by ID from the API.
     */
    suspend fun getCourseById(id: String): CourseApiDto {
        return courseApiService.getCourseById(id)
    }

    /**
     * Implements CREATE (POST) via the API.
     */
    suspend fun createCourse(courseDto: CourseCreateApiDto): CourseApiDto {
        return courseApiService.createCourse(courseDto)
    }

    /**
     * Implements UPDATE (PUT) via the API.
     */
    suspend fun updateCourse(courseDto: CourseApiDto): CourseApiDto {
        // We use the ID from the DTO in the path, and the DTO body for the update fields
        return courseApiService.updateCourse(courseDto.id, courseDto)
    }

    /**
     * Implements DELETE via the API.
     */
    suspend fun deleteCourse(id: String) {
        courseApiService.deleteCourse(id)
    }
}