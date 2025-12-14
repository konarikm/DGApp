package cz.utb.fai.dgapp.domain

import java.time.LocalDate

data class Round (
    val id: String,
    val player: Player,
    val course: Course,
    val scores: List<Int>,
    val date: LocalDate,
    val totalScore: Int,
    val totalPar: Int,
    val parScore: Int
) {
    init {
        // Validation check, though it should be handled by the backend/repo
        require(scores.size == course.numberOfHoles) { "Scores length must match course holes." }
    }
}