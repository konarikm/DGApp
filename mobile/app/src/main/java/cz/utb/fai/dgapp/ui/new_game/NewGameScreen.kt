package cz.utb.fai.dgapp.ui.new_game

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Main screen for initiating a new disc golf round.
 * This screen handles course selection and player identification before starting scoring.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameScreen(
    uiState: NewGameUiState,
    onStartRound: (NewGameFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    // Internal state for the form inputs
    var formState by remember { mutableStateOf(NewGameFormState()) }

    // State for the Course Selection Dropdown
    var expanded by remember { mutableStateOf(false) }

    // Derived state
    val selectedCourse = uiState.courses.find { it.id == formState.selectedCourseId }
    val isStartEnabled = formState.selectedCourseId != null && formState.playerName.isNotBlank() && !uiState.isLoading

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("New Game") })
        }
    ) { padding ->
        Box(modifier = modifier.padding(padding).fillMaxSize()) {

            // Show loading or error states
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.errorMessage != null -> {
                    Text("Error loading courses: ${uiState.errorMessage}", modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    // Main Form Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Text(
                            text = "Start a new round",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )

                        // --- 1. Player Name Input ---
                        OutlinedTextField(
                            value = formState.playerName,
                            onValueChange = { formState = formState.copy(playerName = it) },
                            label = { Text("Player Name*") },
                            singleLine = true,

                            // TODO
                            // Remove this when getting Player from DB is implemented
                            readOnly = true,

                            modifier = Modifier.fillMaxWidth()
                        )

                        // --- 2. Course Selection Dropdown ---
                        Text(
                            text = "Select Course*",
                            style = MaterialTheme.typography.titleMedium
                        )

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedCourse?.name ?: "Tap to select a course",
                                onValueChange = {}, // Read-only
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor(PrimaryNotEditable, true).fillMaxWidth(),
                                singleLine = true
                            )

                            // Actual Dropdown Menu
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                uiState.courses.forEach { course ->
                                    DropdownMenuItem(
                                        text = { Text("${course.name} (${course.numberOfHoles} holes)") },
                                        onClick = {
                                            formState = formState.copy(selectedCourseId = course.id)
                                            expanded = false
                                        },
                                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                    )
                                }
                            }
                        } // End DropdownMenuBox

                        // --- 3. Start Button ---
                        Button(
                            onClick = { onStartRound(formState) },
                            enabled = isStartEnabled,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                        ) {
                            Text("START ROUND")
                        }
                    } // End Column
                }
            } // End Box
        }
    }
}