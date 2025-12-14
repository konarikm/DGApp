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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cz.utb.fai.dgapp.domain.Course

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditCourseScreen(
    courseId: String,
    // State of the course currently being edited (fetched from ViewModel)
    uiState: CourseDetailUiState,
    onFetchCourse: (String) -> Unit,
    onBackClick: () -> Unit,
    // Action to save the updated course data
    onUpdateCourse: (Course) -> Unit,
    modifier: Modifier = Modifier
) {
    // 1. Internal state of the form (used for editing inputs)
    var formState by remember(courseId) { mutableStateOf(CourseEditFormState(courseId = courseId)) }

    // 2. State management for loading/saving
    val scrollState = rememberScrollState()

    // --- Side Effects ---
    // A) Fetch data when the screen is first composed or courseId changes
    LaunchedEffect(courseId) {
        onFetchCourse(courseId)
    }

    // B) Update form state when UI state data arrives
    LaunchedEffect(uiState.course) {
        uiState.course?.let { course ->
            // Map the fetched Domain Course data to the mutable EditCourseFormState
            formState = formState.copy(
                name = course.name,
                location = course.location ?: "",
                description = course.description ?: "",
                numberOfHoles = course.numberOfHoles,
                parValues = course.parValues.map { it.toString() } // Convert Ints to Strings for text fields
            )
        }
    }

    // --- Derived State for Validation ---
    val isDataLoaded = uiState.course != null && !uiState.isLoading
    val isFormValid = formState.name.isNotBlank() && formState.areParValuesValid && isDataLoaded

    // --- UI Structure ---
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit course") },
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
                        // Map formState back to Domain Course object for the PUT request
                        val updatedCourse = uiState.course!!.copy(
                            name = formState.name,
                            location = formState.location,
                            description = formState.description,
                            parValues = formState.getIntParValues()
                            // Note: NumberOfHoles is not typically edited directly via form state
                        )
                        onUpdateCourse(updatedCourse)
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
                Spacer(Modifier.Companion.height(16.dp))

                // --- BASIC INFO ---
                OutlinedTextField(
                    value = formState.name,
                    onValueChange = {
                        formState = formState.copy(
                            name = it,
                            location = formState.location,
                            description = formState.description
                        )
                    },
                    label = { Text("Course Name*") },
                    singleLine = true,
                    modifier = Modifier.Companion.fillMaxWidth()
                )
                Spacer(Modifier.Companion.height(8.dp))
                OutlinedTextField(
                    value = formState.location,
                    onValueChange = { formState = formState.copy(location = it) },
                    label = { Text("Location") },
                    singleLine = true,
                    modifier = Modifier.Companion.fillMaxWidth()
                )
                Spacer(Modifier.Companion.height(8.dp))
                OutlinedTextField(
                    value = formState.description,
                    onValueChange = { formState = formState.copy(description = it) },
                    label = { Text("Description") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.Companion.fillMaxWidth()
                )
                Spacer(Modifier.Companion.height(24.dp))

                // --- DYNAMIC PAR INPUTS ---
                Text(
                    text = "Edit Par Values (min=3, max=5)*",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.Companion.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    // Constrain height within the scrolling Column
                    modifier = Modifier.Companion
                        .heightIn(max = 800.dp)
                        .fillMaxWidth()
                ) {
                    itemsIndexed(formState.parValues) { index, parValueString ->
                        val holeNumber = index + 1

                        OutlinedTextField(
                            value = parValueString,
                            onValueChange = { newParValue ->
                                // Only allow one digit (3, 4, or 5)
                                val sanitizedPar = newParValue.filter { it.isDigit() }.take(1)

                                val updatedParValues = formState.parValues.toMutableList()
                                updatedParValues[index] = sanitizedPar

                                formState = formState.copy(parValues = updatedParValues)
                            },
                            label = { Text("Hole $holeNumber") },
                            // Show error if value is outside 3-5 range
                            isError = parValueString.toIntOrNull() !in 3..5,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Number),
                            singleLine = true,
                            modifier = Modifier.Companion.height(60.dp)
                        )
                    }
                }
                Spacer(Modifier.Companion.height(16.dp))
            }
        }
    }
}