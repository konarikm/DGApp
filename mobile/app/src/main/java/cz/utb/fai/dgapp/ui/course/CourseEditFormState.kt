package cz.utb.fai.dgapp.ui.course

import java.util.Collections

data class CourseEditFormState(
    val courseId: String = "",
    val name: String = "",
    val location: String = "",
    val description: String = "",
    val numberOfHoles: Int = 9,
    val parValues: List<String> = Collections.nCopies(9, "3") // Par values as strings for editing
) {
    /** Helper to check if Par values are valid (3-5). */
    val areParValuesValid: Boolean
        get() = parValues.all { it.toIntOrNull() in 3..5 }

    /** Helper to convert string pars back to Ints for saving. */
    fun getIntParValues(): List<Int> = parValues.mapNotNull { it.toIntOrNull() }
}