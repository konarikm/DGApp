package cz.utb.fai.dgapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import java.util.Collections

/**
 * Data class to hold the mutable state of the new course form.
 * Uses Int for Par, initialized to default par (3).
 */
data class NewCourseFormState(
    val name: String = "",
    val location: String = "",
    val description: String = "",
    val numberOfHoles: Int = 9
) {
    /**
     * Helper to generate the default list of Par 3 values based on the hole count.
     */
    val defaultParValues: List<Int>
        get() = Collections.nCopies(numberOfHoles, 3)
}


/**
 * Screen for entering details of a new disc golf course.
 * * @param onBackClick Action to return to the Courses list.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNewCourseScreen(
    onBackClick: () -> Unit,
    // Future: onSaveCourse: (NewCourseFormState) -> Unit,
    modifier: Modifier = Modifier
) {
    // State of the entire form, preserved across configuration changes
    var formState by remember { mutableStateOf(NewCourseFormState()) }

    // Derived state for simple validation
    // Only require the name to be filled, as par values are generated automatically.
    val isFormValid = formState.name.isNotBlank()

    // Scroll state for the main column
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Course") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            // Save Button in a Bottom Bar
            BottomAppBar {
                Button(
                    onClick = {
                        // Generate final list of PAR values (all 3s)
                        val parValuesToSave = formState.defaultParValues

                        // Future: Logic to call ViewModel to save the course
                        // onSaveCourse(formState, parValuesToSave)
                        println("Attempting to save: ${formState.name} with ${formState.numberOfHoles} holes and Pars: $parValuesToSave")
                    },
                    enabled = isFormValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text("Save")
                }
            }
        }
    ) { padding ->
        // Content: Placeholder for the form fields
        Column (
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // --- BASIC INFO ---
            OutlinedTextField(
                value = formState.name,
                onValueChange = { formState = formState.copy(name = it) },
                label = { Text("Course Name*") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()

            )

            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.location,
                onValueChange = { formState = formState.copy(location = it) },
                label = { Text("Location") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = formState.description,
                onValueChange = { formState = formState.copy(description = it) },
                label = { Text("Description") },
                minLines = 3,
                maxLines = 5,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(24.dp))

            // --- HOLE COUNT TOGGLE ---
            Text(
                text = "Number of Holes:",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Button for 9 holes
                OutlinedButton(
                    onClick = {
                        formState = formState.copy(numberOfHoles = 9)
                    },
                    colors = if (formState.numberOfHoles == 9)
                        ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    else ButtonDefaults.outlinedButtonColors()
                ) { Text("9 Holes") }

                // Button for 18 holes
                OutlinedButton(
                    onClick = {
                        formState = formState.copy(numberOfHoles = 18)
                    },
                    colors = if (formState.numberOfHoles == 18)
                        ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    else ButtonDefaults.outlinedButtonColors()
                ) { Text("18 Holes") }
            }
            Spacer(Modifier.height(24.dp))

            // --- PAR INFO DISPLAY (Simplification) ---
            Text(
                text = "Par will be automatically set to 3 for all ${formState.numberOfHoles} holes.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}