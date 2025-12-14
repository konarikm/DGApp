package cz.utb.fai.dgapp.ui.round

import java.time.LocalDate

data class RoundEditFormState(
    val roundId: String = "",
    val courseName: String = "",
    val numberOfHoles: Int = 0,
    val date: LocalDate = LocalDate.now(),
    val scores: List<String> = emptyList()
) {
    /** Helper to check if scores are valid (positive integers). */
    val areScoresValid: Boolean
        get() = scores.all { it.toIntOrNull() != null && it.toIntOrNull()!! > 0 }

    /** Helper to convert string scores back to Ints for saving. */
    fun getIntScores(): List<Int> = scores.mapNotNull { it.toIntOrNull() }
}