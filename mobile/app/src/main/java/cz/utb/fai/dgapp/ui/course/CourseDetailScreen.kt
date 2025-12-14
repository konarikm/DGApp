package cz.utb.fai.dgapp.ui.course

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import cz.utb.fai.dgapp.ui.course.CourseDetailUiState

/**
 * Screen for displaying details of a specific course.
 * * @param courseId The ID of the course to be displayed (used for fetching data in a real setup).
 * @param onBackClick Action to return to the list.
 * @param onEditClick Action to navigate to the edit form.
 * @param onDeleteClick Action to delete the course.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseDetailScreen(
    courseId: String,
    uiState: CourseDetailUiState,
    onFetchCourse: (String) -> Unit,
    onBackClick: () -> Unit,
    onEditClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // State to control the visibility of the confirmation dialog
    var showDeleteDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    // Side Effect: Fetch data when the screen is first composed
    LaunchedEffect(courseId) {
        onFetchCourse(courseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Course Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to courses"
                        )
                    }
                },
                actions = {
                    // Only show actions if data is successfully loaded
                    if (uiState.course != null && !uiState.isLoading) {
                        // Action 1: Edit Course
                        IconButton(onClick = { onEditClick(courseId) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Course")
                        }
                        // Action 2: Delete Course
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete Course")
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
                .padding(16.dp),
        ) {
            when {
                // Loading state
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                // Error state
                uiState.errorMessage != null -> {
                    Text("Error loading: ${uiState.errorMessage}")
                }

                // Data loaded successfully
                uiState.course != null -> {
                    val course = uiState.course
                    Column(
                        modifier = Modifier.Companion.fillMaxWidth().verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = course.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Companion.Bold
                            )

                            course.location?.takeIf { it.isNotBlank() }?.let { location ->
                                Text(
                                    text = location,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontStyle = FontStyle.Companion.Italic
                                )
                            }
                        }

                        course.description?.takeIf { it.isNotBlank() }?.let { description ->
                            HorizontalDivider()
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = "Description:",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Companion.Bold
                                )
                                Text(
                                    text = description,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }

                        HorizontalDivider()
                        Text(
                            text = "Number of Holes: ${course.numberOfHoles}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Total Par: ${course.parValues.sum()}",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Spacer(Modifier.Companion.height(16.dp))

                        // --- Dynamic PAR Values Grid ---
                        Text(
                            text = "Par Values by Hole:",
                            style = MaterialTheme.typography.titleMedium
                        )

                        // Using a simple grid of fixed height to display the dynamically indexed PAR values
                        // Use a fixed height to avoid complex nested scrolling issues, relying on the parent Column scroll
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3), // Show 3 columns for a clean grid layout
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            // Set a maximum height to constrain the LazyGrid within the verticalScroll parent
                            modifier = Modifier.Companion
                                .heightIn(max = 400.dp)
                                .fillMaxWidth()
                        ) {
                            // FIX: Use itemsIndexed to correctly get the hole number (index)
                            itemsIndexed(course.parValues) { index, par ->
                                ParValueItem(holeNumber = index + 1, par = par)
                            }
                        }
                        Spacer(Modifier.Companion.height(16.dp))
                    }
                }
                // Initial state (should not happen after LaunchedEffect)
                else -> {
                    Text("Select a course.")
                }
            }
        }
    }

    // --- CONFIRMATION DIALOG ---
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside or presses back
                showDeleteDialog = false
            },
            title = {
                Text("Confirm Deletion")
            },
            text = {
                Text("Do you really want to delete the course? This action cannot be undone!")
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Execute delete action and dismiss dialog
                        onDeleteClick(courseId)
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        // Just dismiss the dialog
                        showDeleteDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}


/**
 * Component to display a single PAR value in the grid.
 */
@Composable
fun ParValueItem(holeNumber: Int, par: Int) {
    Card(
        modifier = Modifier.Companion.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.Companion
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Text(
                text = "Hole $holeNumber",
                style = MaterialTheme.typography.labelSmall
            )
            Text(
                text = par.toString(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Companion.Bold
            )
        }
    }
}