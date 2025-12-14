package cz.utb.fai.dgapp.ui.round

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Screen for displaying details of a specific round.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundDetailScreen(
    roundId: String,
    uiState: RoundDetailUiState,
    onFetchRound: (String) -> Unit,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit, // Passes ID to edit form
    onDeleteClick: (String) -> Unit, // Passes ID for deletion
    modifier: Modifier = Modifier
) {
    // State to control the visibility of the confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    LaunchedEffect(roundId) {
        onFetchRound(roundId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Round detail") }, // Show course name if loaded
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to History"
                        )
                    }
                },
                actions = {
                    // Only show actions if data is successfully loaded
                    if (uiState.round != null && !uiState.isLoading) {
                        // Action 1: Edit Round
                        IconButton(onClick = { onEditClick(roundId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Round")
                        }
                        // Action 2: Delete Round (Opens dialog)
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Round")
                        }
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.errorMessage != null -> {
                    Text("Error while loading round: ${uiState.errorMessage}")
                }

                uiState.round != null -> {
                    val round = uiState.round
                    val parScorePrefix = if (round.parScore > 0) "+" else ""
                    val dateFormatter =
                        DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
                    val formattedDate = round.date.format(dateFormatter)

                    Column(
                        modifier = Modifier.Companion
                            .fillMaxWidth()
                            .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.Companion.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = round.course.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$formattedDate",
                                style = MaterialTheme.typography.titleMedium,
                                fontStyle = FontStyle.Companion.Italic
                            )
                        }

                        HorizontalDivider()
                        Row(
                            modifier = Modifier.Companion.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = round.player.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "$parScorePrefix${round.parScore} (${round.totalScore})",
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        HorizontalDivider()

                        // --- Score List
                        Text(text = "Hole scores:", style = MaterialTheme.typography.titleMedium)

                        // FIX: Use LazyVerticalGrid for score display
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3), // Show 3 columns per row
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.Companion
                                .heightIn(max = 800.dp) // Max height constraint for stability
                                .fillMaxWidth()
                        ) {
                            // Combine scores and parValues using indexed loop
                            itemsIndexed(round.scores) { index, score ->
                                val holeNumber = index + 1
                                // Check for valid par (defensive programming)
                                val par = round.course.parValues.getOrElse(index) { 3 }

                                RoundScoreItem(
                                    holeNumber = holeNumber,
                                    score = score,
                                    par = par
                                )
                            }
                        }


                        Spacer(Modifier.Companion.height(16.dp))
                    }
                }
                // Initial state (should not happen after LaunchedEffect)
                else -> {
                    Text("No round information.")
                }
            }
        }
    }

    // --- CONFIRMATION DIALOG ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm Deletion") },
            text = {
                Text("Do you really want to delete the round? This action cannot be undone!")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Execute delete action and dismiss dialog
                        onDeleteClick(roundId)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

/**
 * Component to display score details for a single hole in the grid.
 */
@Composable
fun RoundScoreItem(holeNumber: Int, score: Int, par: Int) {
    val difference = score - par

    val containerColor = when {
        difference < 0 -> MaterialTheme.colorScheme.primaryContainer // Birdie or better
        difference > 0 -> MaterialTheme.colorScheme.errorContainer // Bogey or worse
        else -> MaterialTheme.colorScheme.surfaceVariant // Par
    }

    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier.Companion
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            // Hole number
            Text(
                text = "Hole $holeNumber (PAR $par)",
                style = MaterialTheme.typography.labelSmall
            )
            Spacer(Modifier.Companion.height(2.dp))
            // Score and Difference
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.Companion.Bottom
            ) {
                // Actual strokes
                Text(
                    text = score.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Companion.Bold
                )
            }
        }
    }
}