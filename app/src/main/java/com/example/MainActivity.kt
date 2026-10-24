package com.example

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QuestionAnswer
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.NoteViewerDialog
import com.example.ui.components.PlayBillingSimulatorSheet
import com.example.ui.components.QaViewerDialog
import com.example.ui.components.SchoolDetailSheet
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.ClassNotesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.MarketplaceBillingScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.QaScreen
import com.example.ui.screens.SchoolSearchScreen
import com.example.ui.theme.AcademicNavy
import com.example.ui.theme.EmeraldTeal
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.AppViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppRoot()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppRoot(viewModel: AppViewModel = viewModel()) {
    val context = LocalContext.current
    val activity = context as? Activity

    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val screenStack by viewModel.screenStack.collectAsStateWithLifecycle()

    val entitlements by viewModel.entitlements.collectAsStateWithLifecycle()
    val unlockedSchoolIds by viewModel.unlockedSchoolIds.collectAsStateWithLifecycle()
    val unlockedNoteIds by viewModel.unlockedNoteIds.collectAsStateWithLifecycle()
    val unlockedQaIds by viewModel.unlockedQaIds.collectAsStateWithLifecycle()

    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedDivision by viewModel.selectedDivision.collectAsStateWithLifecycle()
    val selectedType by viewModel.selectedType.collectAsStateWithLifecycle()

    val filteredNotes by viewModel.filteredNotes.collectAsStateWithLifecycle()
    val notesSearchQuery by viewModel.notesSearchQuery.collectAsStateWithLifecycle()
    val selectedSubject by viewModel.selectedSubject.collectAsStateWithLifecycle()
    val selectedLevel by viewModel.selectedLevel.collectAsStateWithLifecycle()

    val filteredQuestions by viewModel.filteredQuestions.collectAsStateWithLifecycle()
    val qaSearchQuery by viewModel.qaSearchQuery.collectAsStateWithLifecycle()
    val selectedQaCategory by viewModel.selectedQaCategory.collectAsStateWithLifecycle()
    val selectedQaLevel by viewModel.selectedQaLevel.collectAsStateWithLifecycle()

    val billingTab by viewModel.billingTab.collectAsStateWithLifecycle()
    val selectedSchoolPackage by viewModel.selectedSchoolPackage.collectAsStateWithLifecycle()
    val selectedNotePackage by viewModel.selectedNotePackage.collectAsStateWithLifecycle()
    val selectedQaPackage by viewModel.selectedQaPackage.collectAsStateWithLifecycle()
    val isPlayBillingModalOpen by viewModel.isPlayBillingModalOpen.collectAsStateWithLifecycle()

    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val selectedSchool by viewModel.selectedSchool.collectAsStateWithLifecycle()
    val selectedNote by viewModel.selectedNote.collectAsStateWithLifecycle()
    val selectedQa by viewModel.selectedQa.collectAsStateWithLifecycle()

    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Display snackbar toast notifications
    LaunchedEffect(toastMessage) {
        toastMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearToast()
        }
    }

    // Hardware/Gesture Back button handling
    BackHandler(enabled = screenStack.size > 1) {
        viewModel.navigateBack()
    }

    val schoolDetailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val billingSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (currentScreen != AppScreen.ADMIN) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.School,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "EIIN Finder & Notes",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "EIIN • Notes • Q&A Packs",
                                    fontSize = 9.sp,
                                    color = GoldenAmber,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    },
                    actions = {
                        // School Credit Pill
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.18f))
                                .clickable {
                                    viewModel.setBillingTab("SCHOOLS")
                                    viewModel.navigateTo(AppScreen.BILLING)
                                }
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${entitlements.schoolCreditsRemaining}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        // Note Credit Pill
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(EmeraldTeal)
                                .clickable {
                                    viewModel.setBillingTab("NOTES")
                                    viewModel.navigateTo(AppScreen.BILLING)
                                }
                                .padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.MenuBook,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${entitlements.noteCreditsRemaining}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.width(5.dp))

                        // Q&A Credit Pill
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF01875F))
                                .clickable {
                                    viewModel.setBillingTab("QA")
                                    viewModel.navigateTo(AppScreen.BILLING)
                                }
                                .padding(horizontal = 7.dp, vertical = 4.dp)
                                .testTag("top_bar_qa_credits_pill"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.QuestionAnswer,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${entitlements.qaCreditsRemaining} Q&A",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        IconButton(
                            onClick = { viewModel.navigateTo(AppScreen.BILLING) },
                            modifier = Modifier.testTag("nav_tab_billing")
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Google Play Store Packs",
                                tint = GoldenAmber
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = AcademicNavy,
                        titleContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            if (currentScreen != AppScreen.ADMIN) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = AcademicNavy,
                    windowInsets = WindowInsets.navigationBars,
                    modifier = Modifier.testTag("main_bottom_nav_bar")
                ) {
                    NavigationBarItem(
                        selected = currentScreen == AppScreen.HOME,
                        onClick = { viewModel.navigateTo(AppScreen.HOME) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                                contentDescription = "Home"
                            )
                        },
                        label = { Text("Home", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AcademicNavy,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.SEARCH,
                        onClick = { viewModel.navigateTo(AppScreen.SEARCH) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                                contentDescription = "EIIN Search"
                            )
                        },
                        label = { Text("EIIN", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AcademicNavy,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_search")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.NOTES,
                        onClick = { viewModel.navigateTo(AppScreen.NOTES) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.NOTES) Icons.Filled.MenuBook else Icons.Outlined.MenuBook,
                                contentDescription = "Class Notes"
                            )
                        },
                        label = { Text("Notes", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldTeal,
                            indicatorColor = EmeraldTeal.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_notes")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.QA,
                        onClick = { viewModel.navigateTo(AppScreen.QA) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.QA) Icons.Filled.QuestionAnswer else Icons.Outlined.QuestionAnswer,
                                contentDescription = "Q&A Pack"
                            )
                        },
                        label = { Text("Q&A", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color(0xFF01875F),
                            indicatorColor = Color(0xFF01875F).copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_tab_qa")
                    )

                    NavigationBarItem(
                        selected = currentScreen == AppScreen.PROFILE,
                        onClick = { viewModel.navigateTo(AppScreen.PROFILE) },
                        icon = {
                            Icon(
                                imageVector = if (currentScreen == AppScreen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                                contentDescription = "Profile"
                            )
                        },
                        label = { Text("Profile", fontSize = 10.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = AcademicNavy,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_tab_profile")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(
                    entitlements = entitlements,
                    unlockedSchoolIds = unlockedSchoolIds,
                    unlockedNoteIds = unlockedNoteIds,
                    schools = searchResults,
                    notes = filteredNotes,
                    onNavigateToSearch = { viewModel.navigateTo(AppScreen.SEARCH) },
                    onNavigateToNotes = { viewModel.navigateTo(AppScreen.NOTES) },
                    onNavigateToQa = { viewModel.navigateTo(AppScreen.QA) },
                    onNavigateToBilling = { tab ->
                        viewModel.setBillingTab(tab)
                        viewModel.navigateTo(AppScreen.BILLING)
                    },
                    onSelectSchool = { school -> viewModel.onSelectSchool(school) },
                    onSelectNote = { note -> viewModel.onSelectNote(note) },
                    onCopyEiin = { eiin, name -> viewModel.copyEiinToClipboard(eiin, name) }
                )

                AppScreen.SEARCH -> SchoolSearchScreen(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.onSearchQueryChanged(it) },
                    selectedDivision = selectedDivision,
                    onSelectDivision = { viewModel.onSelectDivision(it) },
                    selectedType = selectedType,
                    onSelectType = { viewModel.onSelectType(it) },
                    searchResults = searchResults,
                    unlockedSchoolIds = unlockedSchoolIds,
                    remainingCredits = entitlements.schoolCreditsRemaining,
                    onSelectSchool = { school -> viewModel.onSelectSchool(school) },
                    onCopyEiin = { eiin, name -> viewModel.copyEiinToClipboard(eiin, name) },
                    onNavigateToBilling = {
                        viewModel.setBillingTab("SCHOOLS")
                        viewModel.navigateTo(AppScreen.BILLING)
                    }
                )

                AppScreen.NOTES -> ClassNotesScreen(
                    notesSearchQuery = notesSearchQuery,
                    onNotesSearchChange = { viewModel.onNotesSearchChanged(it) },
                    selectedSubject = selectedSubject,
                    onSelectSubject = { viewModel.onSelectSubject(it) },
                    selectedLevel = selectedLevel,
                    onSelectLevel = { viewModel.onSelectLevel(it) },
                    notes = filteredNotes,
                    unlockedNoteIds = unlockedNoteIds,
                    remainingNoteCredits = entitlements.noteCreditsRemaining,
                    onSelectNote = { note -> viewModel.onSelectNote(note) },
                    onUnlockNote = { note -> viewModel.onUnlockNote(note) },
                    onNavigateToBilling = {
                        viewModel.setBillingTab("NOTES")
                        viewModel.navigateTo(AppScreen.BILLING)
                    }
                )

                AppScreen.QA -> QaScreen(
                    qaSearchQuery = qaSearchQuery,
                    onQaSearchChange = { viewModel.onQaSearchChanged(it) },
                    selectedCategory = selectedQaCategory,
                    onSelectCategory = { viewModel.onSelectQaCategory(it) },
                    selectedLevel = selectedQaLevel,
                    onSelectLevel = { viewModel.onSelectQaLevel(it) },
                    questions = filteredQuestions,
                    unlockedQaIds = unlockedQaIds,
                    remainingQaCredits = entitlements.qaCreditsRemaining,
                    onSelectQa = { qa -> viewModel.onSelectQa(qa) },
                    onUnlockQa = { qa -> viewModel.onUnlockQa(qa) },
                    onAskQuestion = { title, details, cat, lvl ->
                        viewModel.onAskNewQuestion(title, details, cat, lvl)
                    },
                    onNavigateToBilling = {
                        viewModel.setBillingTab("QA")
                        viewModel.navigateTo(AppScreen.BILLING)
                    }
                )

                AppScreen.BILLING -> MarketplaceBillingScreen(
                    currentTab = billingTab,
                    onTabSelected = { viewModel.setBillingTab(it) },
                    entitlements = entitlements,
                    onPurchaseSchoolPackage = { pkg -> viewModel.initiateSchoolPurchase(pkg, activity) },
                    onPurchaseNotePackage = { pkg -> viewModel.initiateNotePurchase(pkg, activity) },
                    onPurchaseQaPackage = { pkg -> viewModel.initiateQaPurchase(pkg, activity) }
                )

                AppScreen.PROFILE -> ProfileScreen(
                    entitlements = entitlements,
                    unlockedSchoolIds = unlockedSchoolIds,
                    unlockedNoteIds = unlockedNoteIds,
                    unlockedQaIds = unlockedQaIds,
                    allSchools = searchResults,
                    allNotes = filteredNotes,
                    allQuestions = filteredQuestions,
                    transactions = transactions,
                    onSelectSchool = { school -> viewModel.onSelectSchool(school) },
                    onSelectNote = { note -> viewModel.onSelectNote(note) },
                    onSelectQa = { qa -> viewModel.onSelectQa(qa) },
                    onRestorePurchases = { viewModel.restorePurchases() },
                    onNavigateToAdmin = { viewModel.navigateTo(AppScreen.ADMIN) }
                )

                AppScreen.ADMIN -> AdminDashboardScreen(
                    schools = searchResults,
                    notes = filteredNotes,
                    transactions = transactions,
                    onBack = { viewModel.navigateBack() },
                    onAddSchool = { school -> viewModel.adminAddSchool(school) },
                    onDeleteSchool = { id -> viewModel.adminDeleteSchool(id) },
                    onAddNote = { note -> viewModel.adminAddNote(note) },
                    onResetDefaults = { viewModel.adminResetDefaults() }
                )
            }
        }
    }

    // Modal Sheet 1: School Detail Dossier & EIIN Reveal
    selectedSchool?.let { school ->
        val isUnlocked = unlockedSchoolIds.contains(school.id)
        SchoolDetailSheet(
            school = school,
            isUnlocked = isUnlocked,
            remainingCredits = entitlements.schoolCreditsRemaining,
            onDismiss = { viewModel.onSelectSchool(null) },
            onUnlock = { viewModel.onUnlockSchool(school) },
            onCopyEiin = { eiin -> viewModel.copyEiinToClipboard(eiin, school.name) },
            onOpenBilling = {
                viewModel.onSelectSchool(null)
                viewModel.setBillingTab("SCHOOLS")
                viewModel.navigateTo(AppScreen.BILLING)
            },
            sheetState = schoolDetailSheetState
        )
    }

    // Modal Sheet 2: Google Play Billing Simulator Sheet (Google Play Only)
    if (isPlayBillingModalOpen) {
        PlayBillingSimulatorSheet(
            schoolPackage = selectedSchoolPackage,
            notePackage = selectedNotePackage,
            qaPackage = selectedQaPackage,
            onDismiss = { viewModel.dismissBillingModal() },
            onCompletePurchase = { outcome -> viewModel.completePurchaseWithOutcome(outcome) },
            sheetState = billingSheetState
        )
    }

    // Modal Dialog 3: Class Note Viewer & Preview Reader
    selectedNote?.let { note ->
        val isUnlocked = unlockedNoteIds.contains(note.id)
        NoteViewerDialog(
            note = note,
            isUnlocked = isUnlocked,
            remainingNoteCredits = entitlements.noteCreditsRemaining,
            onClose = { viewModel.closeNoteReader() },
            onUnlock = { viewModel.onUnlockNote(note) },
            onOpenBilling = {
                viewModel.closeNoteReader()
                viewModel.setBillingTab("NOTES")
                viewModel.navigateTo(AppScreen.BILLING)
            }
        )
    }

    // Modal Dialog 4: Question & Answer (Q&A) Solution Viewer
    selectedQa?.let { qa ->
        val isUnlocked = unlockedQaIds.contains(qa.id)
        QaViewerDialog(
            qa = qa,
            isUnlocked = isUnlocked,
            remainingQaCredits = entitlements.qaCreditsRemaining,
            onClose = { viewModel.onSelectQa(null) },
            onUnlock = { viewModel.onUnlockQa(qa) },
            onOpenBilling = {
                viewModel.onSelectQa(null)
                viewModel.setBillingTab("QA")
                viewModel.navigateTo(AppScreen.BILLING)
            }
        )
    }
}
