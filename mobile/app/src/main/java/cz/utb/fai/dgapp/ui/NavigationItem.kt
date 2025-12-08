package cz.utb.fai.dgapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.ui.graphics.vector.ImageVector



/**
 * Sealed class defining all destinations in the bottom navigation bar.
 * This centralizes the route path, icon, and label for each tab.
 */
sealed class NavigationItem(var route: String, var icon: ImageVector, var title: String) {
    object History : NavigationItem("history", Icons.Default.DateRange, "History")

    object NewGame : NavigationItem("new_game", Icons.Default.AddCircle, "New Game")

    object Courses : NavigationItem("courses", Icons.Default.Map, "Courses")
}