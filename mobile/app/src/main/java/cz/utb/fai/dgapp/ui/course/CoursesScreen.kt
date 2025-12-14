package cz.utb.fai.dgapp.ui.course

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.utb.fai.dgapp.domain.Course
import cz.utb.fai.dgapp.ui.course.CoursesUiState

/**
 * Dedicated screen for viewing and managing disc golf courses.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    uiState: CoursesUiState,
    onRefresh: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onAddNewCourseClick: () -> Unit,
    onClearSaveStatus: () -> Unit,
    onCourseClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val snackbarHostState = remember { SnackbarHostState() }

    uiState.saveSuccessMessage?.let { message ->
        LaunchedEffect(message) {
            // Display the message
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            // Clear the message in ViewModel after showing to prevent continuous display
            onClearSaveStatus()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Courses") },
                actions = {
                    // 1. Add New Course Button (Primary action, always visible)
                    IconButton(onClick = onAddNewCourseClick) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add New Course",
                        )
                    }
                    // 2. Refresh Button (Secondary action, hidden during search)
                    if (uiState.searchQuery.isEmpty()) {
                        IconButton(onClick = onRefresh) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh Courses",
                            )
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
                .padding(horizontal = 16.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search course by name") },
                singleLine = true,
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Search")
                        }
                    }
                },
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
            )

            // Content based on state
            Box(modifier = Modifier.Companion.fillMaxSize()) {
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
                                items = uiState.courses,
                                key = { course -> course.id },
                            ) { course ->
                                CourseItem(
                                    course = course,
                                    onItemClick = onCourseClick
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CourseItem(course: Course, onItemClick: (String) -> Unit) {
    val totalPar = course.parValues.sum()

    Card(
        modifier = Modifier.Companion
            .fillMaxWidth()
            .clickable { onItemClick(course.id) }
    ) {
        Column(modifier = Modifier.Companion.padding(12.dp)) {
            Text(
                text = course.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Companion.Bold
            )
            course.location?.let { Text(text = it, fontStyle = FontStyle.Companion.Italic) }
            Text(text = "Par: $totalPar")
        }
    }
}