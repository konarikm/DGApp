package cz.utb.fai.dgapp.ui.course

import java.util.Collections

/**
 * Data class to hold the mutable state of the new course form.
 * Uses Int for Par, initialized to default par (3).
 */
data class NewCourseFormState(
    val name: String = "",
    val location: String = "",
    val description: String = "",
    val numberOfHoles: Int = 9
) {
    /**
     * Helper to generate the default list of Par 3 values based on the hole count.
     */
    val defaultParValues: List<Int>
        get() = Collections.nCopies(numberOfHoles, 3)
}