package cz.utb.fai.dgapp.data.mappers

import cz.utb.fai.dgapp.data.local.Converters
import cz.utb.fai.dgapp.data.local.RoundEntity
import cz.utb.fai.dgapp.data.local.RoundWithDetails
import cz.utb.fai.dgapp.data.remote.RoundApiDto
import cz.utb.fai.dgapp.data.remote.RoundCreateApiDto
import cz.utb.fai.dgapp.data.remote.RoundUpdatedApiDto
import cz.utb.fai.dgapp.domain.Round
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

// Import and rename helper functions from PlayerMappers.kt and CourseMappers.kt
import cz.utb.fai.dgapp.data.mappers.toDomain as toDomainPlayer
import cz.utb.fai.dgapp.data.mappers.toDomain as toDomainCourse

/**
 * Helper to handle serialization/deserialization of List<Int> (scores/parValues) to JSON strings.
 */
private val scoreConverters = Converters()

// REST -> Domain
fun RoundApiDto.toDomain() : Round {
    // 1. Convert nested objects using dedicated mappers
    val domainPlayer = this.player.toDomainPlayer()
    val domainCourse = this.course.toDomainCourse()

    // 2. Parse date string (ISO 8601 from API) to LocalDate
    // We assume the date string is in ISO 8601 format (e.g., "2025-12-13T12:00:00.000Z")
    val localDate = try {
        Instant.parse(this.date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    } catch (e: Exception) {
        // Fallback for dates that might already be in YYYY-MM-DD format
        LocalDate.parse(this.date.substringBefore('T'))
    }

    // 3. Calculate business logic fields
    val totalPar = domainCourse.parValues.sum()
    val totalScore = this.scores.sum()

    return Round(
        id = this.id,
        player = domainPlayer,
        course = domainCourse,
        scores = this.scores,
        date = localDate,
        totalPar = totalPar,
        totalScore = totalScore,
        parScore = totalScore - totalPar
    )
}

// Domain -> REST
/**
 * Converts Domain model (which contains ID) to RoundApiDto for PUT/UPDATE requests.
 * Uses the ID and simplified FKs (IDs only) and scores.
 */
fun Round.toApiDto(): RoundApiDto {
    return RoundApiDto(
        id = this.id,
        player = this.player.toApiDto(),
        course = this.course.toApiDto(),
        scores = this.scores,
        date = this.date.toString()
    )
}

/**
 * Converts Domain model to RoundCreateApiDto for POST/CREATE requests (no ID).
 */
fun Round.toCreateApiDto(): RoundCreateApiDto {
    return RoundCreateApiDto(
        player = this.player.id,
        course = this.course.id,
        scores = this.scores,
    )
}

// Local -> Domain
/**
 * Converts the full Room join structure (RoundEntity) to the Domain model (Round).
 */
fun RoundWithDetails.toDomain() : Round {
    // 1. Deserialize scores from JSON string stored in RoundEntity
    val scoresList = scoreConverters.toIntList(this.round.scoresJson)

    // 2. Convert nested entities to domain objects
    val domainPlayer = this.player.toDomainPlayer()
    // This helper automatically deserializes parValuesJson from CourseEntity
    val domainCourse = this.course.toDomainCourse()

    // 3. Parse date string (YYYY-MM-DD from entity) to LocalDate
    val localDate = LocalDate.parse(this.round.date)

    // 4. Calculate business logic fields
    val totalPar = domainCourse.parValues.sum()
    val totalScore = scoresList.sum()

    return Round(
        id = this.round.id,
        player = domainPlayer,
        course = domainCourse,
        scores = scoresList,
        date = localDate,
        totalPar = totalPar,
        totalScore = totalScore,
        parScore = totalScore - totalPar
    )
}

// Domain -> Local
fun Round.toEntity() : RoundEntity {
    // Convert LocalDate back to String (YYYY-MM-DD) for Room storage.
    val dateString = this.date.toString()

    return RoundEntity(
        id = this.id,
        // Store only the foreign keys (IDs)
        playerId = this.player.id,
        courseId = this.course.id,
        // Serialize scores array to JSON string for SQLite
        scoresJson = scoreConverters.fromIntList(this.scores),
        date = dateString // Store as String
    )
}