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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.utb.fai.dgapp.domain.Round
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRoundScreen(
    roundId: String,
    uiState: RoundDetailUiState,
    onFetchRound: (String) -> Unit,
    onBackClick: () -> Unit,
    onUpdateRound: (Round) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Internal state of the form
    var formState by remember(roundId) { mutableStateOf(RoundEditFormState(roundId = roundId)) }

    // 2. State management for loading/saving
    val scrollState = rememberScrollState()

    // --- Side Effects ---

    // A) Fetch data when the screen is first composed or roundId changes
    LaunchedEffect(roundId) {
        onFetchRound(roundId)
    }

    // B) Update form state when UI state data arrives
    LaunchedEffect(uiState.round) {
        uiState.round?.let { round ->
            // Map the fetched Domain Round data to the mutable EditRoundFormState
            formState = formState.copy(
                courseName = round.course.name,
                numberOfHoles = round.course.numberOfHoles,
                date = round.date,
                scores = round.scores.map { it.toString() } // Convert Ints to Strings for text fields
            )
        }
    }

    // --- Derived State for Validation ---
    val isDataLoaded = uiState.round != null && !uiState.isLoading
    val isFormValid = formState.areScoresValid && isDataLoaded && formState.scores.size == formState.numberOfHoles

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit round") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                Button(
                    onClick = {
                        // Map formState back to Domain Round object for the PUT request
                        val originalRound = uiState.round!!

                        // Create the updated round object with new scores, keeping original player/course
                        val updatedRound = originalRound.copy(
                            scores = formState.getIntScores(),
                            date = formState.date,
                            totalScore = formState.getIntScores().sum(),
                            parScore = formState.getIntScores()
                                .sum() - originalRound.course.parValues.sum()
                        )
                        onUpdateRound(updatedRound)
                    },
                    enabled = isFormValid && !uiState.isLoading,
                    modifier = Modifier.Companion
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Save Changes")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.Companion.fillMaxWidth())
            }
            if (uiState.errorMessage != null) {
                Text("Error loading data: ${uiState.errorMessage}")
            }

            // Only show form fields if data is successfully loaded
            if (isDataLoaded) {
                val round = uiState.round
                val dateFormatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.getDefault())
                val formattedDate = round.date.format(dateFormatter)
                val parScorePrefix = if (round.parScore > 0) "+" else ""

                Spacer(Modifier.Companion.height(16.dp))

                // --- READ-ONLY ROUND DETAILS ---
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.Companion.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = round.course.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Companion.Bold
                        )
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.titleMedium,
                            fontStyle = FontStyle.Companion.Italic
                        )
                    }

                    HorizontalDivider()

                    Row(
                        modifier = Modifier.Companion.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = round.player.name, style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "$parScorePrefix${round.parScore} (${round.totalScore})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Companion.Bold
                        )
                    }
                    HorizontalDivider()
                }

                Spacer(Modifier.Companion.height(24.dp))

                // --- SCORE INPUTS ---
                Text(
                    text = "Edit hole scores:*",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.Companion.height(12.dp))

                if (formState.scores.size != formState.numberOfHoles) {
                    Text("Loading scores...", color = MaterialTheme.colorScheme.secondary)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        // Constrain height within the scrolling Column
                        modifier = Modifier.Companion
                            .heightIn(max = 800.dp)
                            .fillMaxWidth()
                    ) {
                        itemsIndexed(formState.scores) { index, scoreValueString ->
                            val holeNumber = index + 1

                            OutlinedTextField(
                                value = scoreValueString,
                                onValueChange = { newScoreValue ->
                                    // Only allow digits
                                    val sanitizedScore = newScoreValue.filter { it.isDigit() }
                                        .take(2) // Max 2 digits per hole

                                    val updatedScores = formState.scores.toMutableList()
                                    updatedScores[index] = sanitizedScore

                                    formState = formState.copy(scores = updatedScores)
                                },
                                label = { Text("Hole $holeNumber") },
                                // Show error if value is zero or non-numeric
                                isError = scoreValueString.toIntOrNull() == null || scoreValueString.toIntOrNull()!! <= 0,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Number),
                                singleLine = true,
                                modifier = Modifier.height(60.dp)
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }
    }
}