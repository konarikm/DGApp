package cz.utb.fai.dgapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * The root composable that handles primary navigation (Bottom Bar).
 */
@Composable
fun RootScreen() {
    // Simulate navigation state (In a real app, use NavController)
    var currentRoute by remember { mutableStateOf(NavigationItem.NewGame.route) }

    var showAddNewCourseForm by remember { mutableStateOf(false) }


    Scaffold(
        bottomBar = {
            // Hide the bottom bar when navigating to the Add New Course form
            if (!showAddNewCourseForm) {
                NavigationBar(
                    currentRoute = currentRoute,
                    onItemSelected = { item -> currentRoute = item.route }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (currentRoute) {
                NavigationItem.History.route -> {
                    val vm: RoundsHistoryViewModel = viewModel(factory = RoundsHistoryViewModel.Factory)

                    RoundsHistoryScreen(
                        uiState = vm.uiState.collectAsState().value,
                        onRefresh = { vm.loadRounds(forceRefresh = true) },
                        modifier = Modifier.fillMaxSize()
                    )
                }
                NavigationItem.NewGame.route -> {
                    // Displays the combined Home/New Game Screen
                    NewGameScreen(modifier = Modifier.fillMaxSize())
                }
                NavigationItem.Courses.route -> {
                    if (showAddNewCourseForm) {
                        // Display the Add New Course form
                        AddNewCourseScreen(
                            // When 'Back' button is clicked on the form, reset the state to show the list
                            onBackClick = { showAddNewCourseForm = false },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Display the Courses List Screen
                        val vm: CoursesViewModel = viewModel(factory = CoursesViewModel.Factory)

                        CoursesScreen(
                            uiState = vm.uiState.collectAsState().value,
                            onRefresh = { vm.refreshCourses() },
                            onSearchQueryChange = { vm.onSearchQueryChange(it) },
                            onAddNewCourseClick = { showAddNewCourseForm = true },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}