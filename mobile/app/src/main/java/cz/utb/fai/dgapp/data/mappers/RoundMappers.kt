package cz.utb.fai.dgapp.data.mappers

import cz.utb.fai.dgapp.data.local.Converters
import cz.utb.fai.dgapp.data.local.RoundEntity
import cz.utb.fai.dgapp.data.local.RoundWithDetails
import cz.utb.fai.dgapp.data.remote.RoundApiDto
import cz.utb.fai.dgapp.domain.Round

private val scoreConverters = Converters()


// REST -> Domain
fun RoundApiDto.toDomain() : Round {
    val domainPlayer = this.player.toDomain()
    val domainCourse = this.course.toDomain()

    val totalPar = domainCourse.parValues.sum()
    val totalScore = this.scores.sum()

    return Round(
        id = id,
        player = domainPlayer,
        course = domainCourse,
        scores = this.scores,
        date = this.date,
        totalPar = totalPar,
        totalScore = totalScore,
        parScore = totalScore - totalPar
    )
}

// Local -> Domain
fun RoundWithDetails.toDomain() : Round {
    val scoresList = scoreConverters.toIntList(this.round.scoresJson)

    val domainPlayer = this.player.toDomain()
    val domainCourse = this.course.toDomain()

    val totalPar = domainCourse.parValues.sum()
    val totalScore = scoresList.sum()

    return Round(
        id = this.round.id,
        player = domainPlayer,
        course = domainCourse,
        scores = scoresList,
        date = this.round.date,
        totalPar = totalPar,
        totalScore = totalScore,
        parScore = totalScore - totalPar
    )
}

// Domain -> Local
fun Round.toEntity() : RoundEntity {

    return RoundEntity(
        id = this.id,
        playerId = this.player.id,
        courseId = this.course.id,
        scoresJson = scoreConverters.fromIntList(this.scores),
        date = this.date
    )
}