package cz.utb.fai.dgapp.ui.new_game

data class NewGameFormState(
    val selectedCourseId: String? = null,

    // Hard coded for now, should be fetched from db / create new player
    val playerName: String = "Martin Konarik"
)
