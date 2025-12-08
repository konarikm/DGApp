package cz.utb.fai.dgapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Main dashboard screen, typically combines the 'New Game' action
 * and may include a simplified view of courses or player stats.
 */
@Composable
fun NewGameScreen(modifier: Modifier = Modifier) {
    // NOTE: This screen acts as the combined 'initial screen' as requested.
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("New Game Screen")
    }
}