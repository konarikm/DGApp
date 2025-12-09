package cz.utb.fai.dgapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cz.utb.fai.dgapp.domain.Course
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.Modifier

/**
 * Dedicated screen for viewing and managing disc golf courses.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoursesScreen(
    uiState: CoursesUiState,
    onRefresh: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Courses") },
                actions = {
                    // Only show the Refresh button if not actively searching
                    if (uiState.searchQuery.isEmpty()) {
                        TextButton(onClick = onRefresh) {
                            Text("Refresh")
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
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 16.dp)
            )

            // Content based on state
            Box (modifier = Modifier.fillMaxSize()) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    uiState.errorMessage != null -> {
                        Text(
                            text = uiState.errorMessage,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = uiState.courses,
                                key = { course -> course.id },
                            ) { course ->
                                CourseItem(course)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun CourseItem(course: Course) {
    val totalPar = course.parValues.sum()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = course.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            course.location?.let { Text(text = it, fontStyle = FontStyle.Italic) }
            Text(text = "Par: $totalPar")

            course.description?.let {
                Text(text = it, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 4.dp))
            }
        }
    }
}