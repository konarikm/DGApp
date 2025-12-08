package cz.utb.fai.dgapp.domain

data class Course (
    val id: String,
    val name: String,
    val location: String?,
    val description: String?,
    val numberOfHoles: Int,
    val parValues: List<Int>
)
