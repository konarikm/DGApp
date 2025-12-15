package cz.utb.fai.dgapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.utb.fai.dgapp.ui.course.AddNewCourseScreen
import cz.utb.fai.dgapp.ui.course.CourseDetailScreen
import cz.utb.fai.dgapp.ui.course.CoursesScreen
import cz.utb.fai.dgapp.ui.course.CoursesViewModel
import cz.utb.fai.dgapp.ui.course.EditCourseScreen
import cz.utb.fai.dgapp.ui.new_game.ActiveGameData
import cz.utb.fai.dgapp.ui.new_game.NewGameScreen
import cz.utb.fai.dgapp.ui.new_game.NewGameViewModel
import cz.utb.fai.dgapp.ui.new_game.ScoringScreen
import cz.utb.fai.dgapp.ui.new_game.ScoringViewModel
import cz.utb.fai.dgapp.ui.round.EditRoundScreen
import cz.utb.fai.dgapp.ui.round.RoundDetailScreen
import cz.utb.fai.dgapp.ui.round.RoundsScreen
import cz.utb.fai.dgapp.ui.round.RoundsViewModel

/**
 * The root composable that handles primary navigation (Bottom Bar).
 */
@Composable
fun RootScreen() {
    // State to manage the currently selected bottom tab route
    var currentRoute by remember { mutableStateOf(NavigationItem.NewGame.route) }

    // State for internal Courses tab navigation (Add/Edit/Detail)
    var showAddNewCourseForm by remember { mutableStateOf(false) }
    var selectedCourseId by remember { mutableStateOf<String?>(null) }
    var isEditingCourse by remember { mutableStateOf(false) }

    // STATES for Rounds tab navigation (Detail/Edit)
    var selectedRoundId by remember { mutableStateOf<String?>(null) }
    var isEditingRound by remember { mutableStateOf(false) }

    // STATES for NewGame tab
    var activeGameData by remember { mutableStateOf<ActiveGameData?>(null) }

    // State to track if we just completed a round and need to force refresh history
    var shouldForceHistoryRefresh by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            // Hide the bottom bar when adding a new course OR viewing a detail OR editing
            if (!showAddNewCourseForm && selectedCourseId == null && !isEditingCourse && selectedRoundId == null && !isEditingRound && activeGameData == null) {
                NavigationBar(
                    currentRoute = currentRoute,
                    onItemSelected = { item -> currentRoute = item.route }
                )
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            // SCORING SCREEN LOGIC
            if (activeGameData != null) {
                // Initialize specific ViewModel for scoring
                val scoringVm: ScoringViewModel = viewModel(factory = ScoringViewModel.Factory)
                val scoringState by scoringVm.uiState.collectAsState()

                ScoringScreen(
                    courseId = activeGameData!!.courseId,
                    playerName = activeGameData!!.playerName,
                    viewModel = scoringVm,
                    uiState = scoringState,
                    onBackClick = {
                        scoringVm.resetSession()
                        activeGameData = null
                    },
                    onRoundFinished = {
                        scoringVm.resetSession() // Reset state when round is completed
                        activeGameData = null // Exit scoring
                        shouldForceHistoryRefresh = true // Signal that history needs API refresh
                        currentRoute = NavigationItem.History.route // Go to history to see the result
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Navigate between tabs if no scoring round is active
                when (currentRoute) {
                    // History
                    NavigationItem.History.route -> {
                        val vm: RoundsViewModel = viewModel(factory = RoundsViewModel.Factory)
                        val uiState by vm.uiState.collectAsState()
                        val snackbarHostState = remember { SnackbarHostState() }

                        // Side effect: Force API refresh if coming back from finished scoring
                        LaunchedEffect(shouldForceHistoryRefresh) {
                            if (shouldForceHistoryRefresh) {
                                vm.loadRounds(forceRefresh = true) // Fetch new list from API
                                vm.setSaveSuccessMessage("New round saved successfully!")
                                shouldForceHistoryRefresh = false // Reset signal
                            }
                        }

                        // Side effect: Handle operation message (delete, edit success)
                        uiState.saveSuccessMessage?.let { message ->
                            LaunchedEffect(message) {
                                if (selectedRoundId != null || isEditingRound) {
                                    selectedRoundId = null
                                    isEditingRound = false
                                }

                                snackbarHostState.showSnackbar(
                                    message = message,
                                    duration = SnackbarDuration.Short
                                )
                                vm.clearSaveStatus() // Clear message after showing
                            }
                        }

                        when {
                            // 1. Show Edit Round Screen
                            isEditingRound && selectedRoundId != null -> {
                                val detailState by vm.roundDetailState.collectAsState()

                                EditRoundScreen(
                                    roundId = selectedRoundId!!,
                                    uiState = detailState,
                                    onFetchRound = { id -> vm.getRound(id) },
                                    onBackClick = {
                                        isEditingRound = false
                                    }, // Go back to Detail Screen
                                    onUpdateRound = { updatedRound -> vm.updateRound(updatedRound) } // Connect update logic
                                )
                            }

                            // 2. Show Round Detail Screen
                            selectedRoundId != null -> {
                                val detailState by vm.roundDetailState.collectAsState()

                                RoundDetailScreen(
                                    roundId = selectedRoundId!!,
                                    uiState = detailState,
                                    onFetchRound = { id -> vm.getRound(id) },
                                    onBackClick = { selectedRoundId = null },
                                    onEditClick = { isEditingRound = true }, // Navigate to edit
                                    onDeleteClick = { id ->
                                        detailState.round?.let { _ ->
                                            vm.deleteRound(id)
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // 3. Show Round List Screen
                            else -> {
                                RoundsScreen(
                                    uiState = uiState,
                                    onRefresh = { vm.loadRounds(forceRefresh = true) },
                                    onRoundClick = { id ->
                                        selectedRoundId = id
                                    }, // NEW: Click navigates to detail
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }

                        SnackbarHost(
                            snackbarHostState,
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }



                    // New Game
                    NavigationItem.NewGame.route -> {
                        val vm: NewGameViewModel = viewModel(factory = NewGameViewModel.Factory)
                        val uiState by vm.uiState.collectAsState()

                        NewGameScreen(
                            uiState = uiState,
                            onStartRound = { formState ->
                                // Capture the form data to start the scoring screen
                                if (formState.selectedCourseId != null) {
                                    activeGameData = ActiveGameData(
                                        courseId = formState.selectedCourseId,
                                        playerName = formState.playerName
                                    )
                                    vm.clearStartStatus()
                                }
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }



                    // Courses
                    NavigationItem.Courses.route -> {
                        val vm: CoursesViewModel = viewModel(factory = CoursesViewModel.Factory)
                        val uiState by vm.uiState.collectAsState()

                        // Side effect: Automatically navigate back to the list after a successful save
                        LaunchedEffect(uiState.saveSuccessMessage) {
                            if (uiState.saveSuccessMessage != null && (showAddNewCourseForm || selectedCourseId != null || isEditingCourse)) {
                                showAddNewCourseForm = false
                                selectedCourseId = null
                                isEditingCourse = false
                            }
                        }

                        when {
                            // 1. Show Edit Course Screen
                            isEditingCourse && selectedCourseId != null -> {
                                val detailState by vm.courseDetailState.collectAsState()

                                EditCourseScreen(
                                    courseId = selectedCourseId!!,
                                    uiState = detailState,
                                    onFetchCourse = { id -> vm.getCourse(id) },
                                    onBackClick = {
                                        isEditingCourse = false
                                    }, // Go back to Detail Screen
                                    onUpdateCourse = { updatedCourse ->
                                        vm.updateCourse(updatedCourse)
                                    }
                                )
                            }

                            // 2. Show Course Detail Screen
                            selectedCourseId != null -> {
                                val detailState by vm.courseDetailState.collectAsState()

                                CourseDetailScreen(
                                    courseId = selectedCourseId!!,
                                    uiState = detailState,
                                    onFetchCourse = { id -> vm.getCourse(id) },
                                    onBackClick = { selectedCourseId = null }, // Back to list
                                    onEditClick = { isEditingCourse = true },
                                    onDeleteClick = { id ->
                                        detailState.course?.let { _ ->
                                            vm.deleteCourse(id)
                                        }
                                    },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // 3. Show Add New Course Form
                            showAddNewCourseForm -> {
                                AddNewCourseScreen(
                                    onBackClick = { showAddNewCourseForm = false },
                                    onSaveCourse = { formState -> vm.saveNewCourse(formState) },
                                    modifier = Modifier.fillMaxSize()
                                )
                            }

                            // 4. Show Courses List Screen
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
}