package cz.utb.fai.dgapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    var selectedCourseId by remember { mutableStateOf<String?>(null) }

    Scaffold(
        bottomBar = {
            // Hide the bottom bar when adding a new course OR viewing a detail
            if (!showAddNewCourseForm && selectedCourseId == null) {
                NavigationBar(
                    currentRoute = currentRoute,
                    onItemSelected = { item -> currentRoute = item.route }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            when (currentRoute) {
                // History
                NavigationItem.History.route -> {
                    val vm: RoundsHistoryViewModel = viewModel(factory = RoundsHistoryViewModel.Factory)
                    val uiState by vm.uiState.collectAsState()

                    RoundsHistoryScreen(
                        uiState = uiState,
                        onRefresh = { vm.loadRounds(forceRefresh = true) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // New Game
                NavigationItem.NewGame.route -> {
                    // Displays the combined Home/New Game Screen
                    NewGameScreen(modifier = Modifier.fillMaxSize())
                }

                // Courses
                NavigationItem.Courses.route -> {
                    val vm: CoursesViewModel = viewModel(factory = CoursesViewModel.Factory)
                    val uiState by vm.uiState.collectAsState()

                    // Side effect: Automatically navigate back to the list after a successful save
                    LaunchedEffect(uiState.saveSuccessMessage) {
                        if (uiState.saveSuccessMessage != null && (showAddNewCourseForm || selectedCourseId != null)) {
                            showAddNewCourseForm = false
                            selectedCourseId = null
                        }
                    }

                    when {
                        // 1. Show Course Detail Screen
                        selectedCourseId != null -> {
                            // Collect the dedicated detail state from ViewModel
                            val detailState by vm.courseDetailState.collectAsState()

                            CourseDetailScreen(
                                courseId = selectedCourseId!!,
                                uiState = detailState,
                                onFetchCourse = { id -> vm.getCourse(id) },
                                onBackClick = { selectedCourseId = null },
                                onEditClick = { id ->
                                    // Future: Handle edit navigation (e.g., navigate to AddNewCourseScreen in edit mode)
                                    println("Editing course: $id")
                                },
                                onDeleteClick = { id ->
                                    // Future: Handle deletion (call VM function)
                                    println("Deleting course: $id")
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // 2. Show Add New Course Form
                        showAddNewCourseForm -> {
                            AddNewCourseScreen(
                                onBackClick = { showAddNewCourseForm = false },
                                onSaveCourse = { formState -> vm.saveNewCourse(formState) },
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        // 3. Show Courses List Screen
                        else -> {
                            CoursesScreen(
                                uiState = uiState,
                                onRefresh = { vm.refreshCourses() },
                                onSearchQueryChange = { vm.onSearchQueryChange(it) },
                                onClearSaveStatus = { vm.clearSaveStatus() },
                                onAddNewCourseClick = { showAddNewCourseForm = true },
                                onCourseClick = { id -> selectedCourseId = id },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }
        }
    }
}