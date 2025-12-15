package cz.utb.fai.dgapp.ui.round

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.utb.fai.dgapp.domain.Round
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Dedicated screen for viewing previous rounds recorded.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundsScreen(
    uiState: RoundsUiState,
    onRefresh: () -> Unit,
    onRoundClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh Rounds")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.Companion.align(Alignment.Companion.Center)
                    )
                }

                uiState.errorMessage != null -> {
                    Text(
                        text = uiState.errorMessage,
                        modifier = Modifier.Companion.align(Alignment.Companion.Center)
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.Companion
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = uiState.rounds,
                            key = { round -> round.id },
                        ) { round ->
                            RoundItem(round, onRoundClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoundItem(round: Round, onRoundClick: (String) -> Unit) {
    val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy", Locale.getDefault())
    val formattedDate = round.date.format(dateFormatter)

    // Determine the prefix for parScore
    val parScorePrefix = if (round.parScore > 0) "+" else ""

    Card(
        modifier = Modifier.Companion
            .fillMaxWidth()
            // Make the entire card clickable and pass the round ID
            .clickable { onRoundClick(round.id) }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = round.course.name, style = MaterialTheme.typography.titleMedium)
                Text(text = formattedDate, fontStyle = FontStyle.Italic)
            }

            Text(text = round.player.name)

            // Displaying Par Score (e.g., +2 or -2) and Total Strokes (e.g., 27)
            Text(
                text = "$parScorePrefix${round.parScore} (${round.totalScore})",
                fontWeight = FontWeight.Bold
            )
        }
    }
}