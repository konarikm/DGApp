package cz.utb.fai.dgapp.ui.new_game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScoringScreen(
    courseId: String,
    playerName: String,
    viewModel: ScoringViewModel, // Injected VM
    uiState: ScoringUiState,
    onBackClick: () -> Unit,
    onRoundFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Initialize session once
    LaunchedEffect(courseId) {
        if (uiState.course == null) {
            viewModel.initializeSession(courseId, playerName)
        }
    }

    // Handle finish navigation
    LaunchedEffect(uiState.isRoundFinished) {
        if (uiState.isRoundFinished) {
            onRoundFinished()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(uiState.course?.name ?: "Scoring", style = MaterialTheme.typography.titleMedium,)
                        Text(uiState.playerName, style = MaterialTheme.typography.bodySmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading || uiState.course == null) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.errorMessage != null) {
                Text(
                    text = "Error: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                // --- SCORING UI CONTENT ---
                val currentIndex = uiState.currentHoleIndex
                val currentHoleNum = currentIndex + 1
                val totalHoles = uiState.course.numberOfHoles
                val currentPar = uiState.course.parValues.getOrElse(currentIndex) { 3 }
                val currentScore = uiState.scores.getOrElse(currentIndex) { currentPar }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {

                    // 1. Hole Info Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp).fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Hole $currentHoleNum",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = "PAR $currentPar",
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 2. Score Controller (The Big Buttons)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Decrease Button
                        FilledIconButton(
                            onClick = { viewModel.updateCurrentHoleScore(-1) },
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Minus")
                        }

                        Spacer(Modifier.width(32.dp))

                        // Score Display
                        Text(
                            text = "$currentScore",
                            fontSize = 96.sp,
                            fontWeight = FontWeight.Bold,
                            color = ScoreColor(currentScore, currentPar)
                        )

                        Spacer(Modifier.width(32.dp))

                        // Increase Button
                        FilledIconButton(
                            onClick = { viewModel.updateCurrentHoleScore(+1) },
                            modifier = Modifier.size(64.dp),
                            shape = CircleShape
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Plus")
                        }
                    }

                    // Score Context (Birdie/Bogey text)
                    Text(
                        text = getScoreTerm(currentScore, currentPar),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    // 3. Navigation Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // PREVIOUS
                        OutlinedButton(
                            onClick = { viewModel.previousHole() },
                            enabled = currentIndex > 0,
                            modifier = Modifier.weight(1f).height(56.dp)
                        ) {
                            Text("Previous")
                        }

                        Spacer(Modifier.width(16.dp))

                        // NEXT / FINISH
                        Button(
                            onClick = {
                                if (currentIndex < totalHoles - 1) {
                                    viewModel.nextHole()
                                } else {
                                    viewModel.finishRound()
                                }
                            },
                            modifier = Modifier.weight(1f).height(56.dp)
                        ) {
                            Text(if (currentIndex < totalHoles - 1) "Next" else "FINISH")
                        }
                    }
                }
            }
        }
    }
}

// Helper for golf terminology
fun getScoreTerm(score: Int, par: Int): String {
    if (score == 1) return "Ace!"
    return when (score - par) {
        -3 -> "Albatross"
        -2 -> "Eagle"
        -1 -> "Birdie"
        0 -> "Par"
        1 -> "Bogey"
        2 -> "Double Bogey"
        3 -> "Triple Bogey"
        else -> if (score < par) "Eagle (-${par-score})" else "+${score-par}"
    }
}

// Helper for color coding
@Composable
fun ScoreColor(score: Int, par: Int): Color {
    return when {
        score < par -> Color(0xFF4FC455) // Green for under par
        score > par -> Color(0xFFAF3636) // Red for over par
        else -> MaterialTheme.colorScheme.onBackground
    }
}