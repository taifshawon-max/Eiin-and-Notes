package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.billing.GooglePlayBillingHelper
import com.example.data.db.AppDatabase
import com.example.data.db.InitialDataSeeder
import com.example.data.model.ClassNoteEntity
import com.example.data.model.NoteCreditPackage
import com.example.data.model.PricingCatalog
import com.example.data.model.PurchaseTransactionEntity
import com.example.data.model.QaCreditPackage
import com.example.data.model.QuestionAnswerEntity
import com.example.data.model.SchoolCreditPackage
import com.example.data.model.SchoolEntity
import com.example.data.model.UserEntitlementEntity
import com.example.data.repository.BillingRepository
import com.example.data.repository.EntitlementRepository
import com.example.data.repository.NotesRepository
import com.example.data.repository.PurchaseResult
import com.example.data.repository.QaRepository
import com.example.data.repository.SchoolRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen(val label: String) {
    HOME("Home"),
    SEARCH("EIIN Search"),
    NOTES("Class Notes"),
    QA("Q&A Pack"),
    BILLING("Get Credits"),
    PROFILE("Profile"),
    ADMIN("Admin Console")
}

class AppViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val schoolRepository = SchoolRepository(db.schoolDao(), db.entitlementDao())
    private val notesRepository = NotesRepository(db.classNoteDao(), db.entitlementDao())
    private val qaRepository = QaRepository(db.questionAnswerDao(), db.entitlementDao())
    private val entitlementRepository = EntitlementRepository(db.entitlementDao())
    private val billingRepository = BillingRepository(db.transactionDao(), entitlementRepository)

    val playBillingHelper = GooglePlayBillingHelper(
        context = application,
        onPurchaseVerified = { sku, purchaseToken, orderId ->
            handleNativePlayPurchase(sku, purchaseToken, orderId)
        },
        onBillingStatusMessage = { msg ->
            _toastMessage.value = msg
        }
    )

    // Navigation stack for back button support
    private val _screenStack = MutableStateFlow(listOf(AppScreen.HOME))
    val screenStack: StateFlow<List<AppScreen>> = _screenStack.asStateFlow()
    val currentScreen: StateFlow<AppScreen> = _screenStack.map { it.lastOrNull() ?: AppScreen.HOME }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppScreen.HOME)

    // Entitlements & Transactions
    val entitlements: StateFlow<UserEntitlementEntity> = entitlementRepository.entitlements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), InitialDataSeeder.getDefaultEntitlements())

    val unlockedSchoolIds: StateFlow<Set<String>> = entitlementRepository.unlockedSchools
        .map { list -> list.map { it.schoolId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val unlockedNoteIds: StateFlow<Set<String>> = entitlementRepository.unlockedNotes
        .map { list -> list.map { it.noteId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val unlockedQaIds: StateFlow<Set<String>> = entitlementRepository.unlockedQa
        .map { list -> list.map { it.qaId }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val transactions: StateFlow<List<PurchaseTransactionEntity>> = billingRepository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Live Google School Search State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchingGoogle = MutableStateFlow(false)
    val isSearchingGoogle: StateFlow<Boolean> = _isSearchingGoogle.asStateFlow()

    private val _googleSearchResults = MutableStateFlow<List<SchoolEntity>>(emptyList())
    val googleSearchResults: StateFlow<List<SchoolEntity>> = _googleSearchResults.asStateFlow()

    private val _googleSuggestions = MutableStateFlow<List<String>>(emptyList())
    val googleSuggestions: StateFlow<List<String>> = _googleSuggestions.asStateFlow()

    private val _activeGoogleWebQuery = MutableStateFlow("")
    val activeGoogleWebQuery: StateFlow<String> = _activeGoogleWebQuery.asStateFlow()

    private val _recentGoogleSearches = MutableStateFlow(
        listOf(
            "Viqarunnisa Noon School EIIN",
            "Rajuk Uttara Model College EIIN",
            "Notre Dame College Dhaka EIIN",
            "Chittagong Collegiate School EIIN",
            "Rajshahi Collegiate School EIIN"
        )
    )
    val recentGoogleSearches: StateFlow<List<String>> = _recentGoogleSearches.asStateFlow()

    private val _selectedDivision = MutableStateFlow("All")
    val selectedDivision: StateFlow<String> = _selectedDivision.asStateFlow()

    private val _selectedType = MutableStateFlow("All")
    val selectedType: StateFlow<String> = _selectedType.asStateFlow()

    private var googleSearchJob: Job? = null

    val allCachedSchools: StateFlow<List<SchoolEntity>> = schoolRepository.allSchools
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /**
     * Prioritizes live Google Search results whenever the user types in the search bar,
     * rather than relying on static local data.
     */
    val searchResults: StateFlow<List<SchoolEntity>> = combine(
        _searchQuery,
        _googleSearchResults,
        _searchQuery.flatMapLatest { schoolRepository.searchSchoolsLocalCache(it) },
        _selectedDivision,
        _selectedType
    ) { query, liveGoogleList, cachedList, div, type ->
        val baseList = if (query.isNotBlank()) {
            // Merge live Google results first, followed by any matching cached records
            (liveGoogleList + cachedList).distinctBy {
                if (it.eiin.length == 6 && it.eiin.all { c -> c.isDigit() }) it.eiin else it.name.lowercase()
            }
        } else {
            if (liveGoogleList.isNotEmpty()) liveGoogleList else cachedList
        }

        baseList.filter { school ->
            (div == "All" || school.division.equals(div, ignoreCase = true) || school.division == "Bangladesh") &&
            (type == "All" || school.institutionType.contains(type, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected School for details sheet
    private val _selectedSchool = MutableStateFlow<SchoolEntity?>(null)
    val selectedSchool: StateFlow<SchoolEntity?> = _selectedSchool.asStateFlow()

    // Notes Marketplace State
    private val _notesSearchQuery = MutableStateFlow("")
    val notesSearchQuery: StateFlow<String> = _notesSearchQuery.asStateFlow()

    private val _selectedSubject = MutableStateFlow("All")
    val selectedSubject: StateFlow<String> = _selectedSubject.asStateFlow()

    private val _selectedLevel = MutableStateFlow("All")
    val selectedLevel: StateFlow<String> = _selectedLevel.asStateFlow()

    val filteredNotes: StateFlow<List<ClassNoteEntity>> = combine(
        _notesSearchQuery,
        _selectedSubject,
        _selectedLevel
    ) { q, subj, lvl -> Triple(q, subj, lvl) }
    .flatMapLatest { (q, subj, lvl) ->
        notesRepository.filterNotes(q, subj, lvl)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected Note for Preview/Reader
    private val _selectedNote = MutableStateFlow<ClassNoteEntity?>(null)
    val selectedNote: StateFlow<ClassNoteEntity?> = _selectedNote.asStateFlow()

    private val _isViewingFullNote = MutableStateFlow(false)
    val isViewingFullNote: StateFlow<Boolean> = _isViewingFullNote.asStateFlow()

    // Q&A Pack State
    private val _qaSearchQuery = MutableStateFlow("")
    val qaSearchQuery: StateFlow<String> = _qaSearchQuery.asStateFlow()

    private val _selectedQaCategory = MutableStateFlow("All")
    val selectedQaCategory: StateFlow<String> = _selectedQaCategory.asStateFlow()

    private val _selectedQaLevel = MutableStateFlow("All")
    val selectedQaLevel: StateFlow<String> = _selectedQaLevel.asStateFlow()

    val filteredQuestions: StateFlow<List<QuestionAnswerEntity>> = combine(
        _qaSearchQuery,
        _selectedQaCategory,
        _selectedQaLevel
    ) { q, cat, lvl -> Triple(q, cat, lvl) }
    .flatMapLatest { (q, cat, lvl) ->
        qaRepository.filterQuestions(q, cat, lvl)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedQa = MutableStateFlow<QuestionAnswerEntity?>(null)
    val selectedQa: StateFlow<QuestionAnswerEntity?> = _selectedQa.asStateFlow()

    // Billing & Purchase State
    private val _billingTab = MutableStateFlow("SCHOOLS") // "SCHOOLS", "NOTES", or "QA"
    val billingTab: StateFlow<String> = _billingTab.asStateFlow()

    private val _selectedSchoolPackage = MutableStateFlow<SchoolCreditPackage?>(null)
    val selectedSchoolPackage: StateFlow<SchoolCreditPackage?> = _selectedSchoolPackage.asStateFlow()

    private val _selectedNotePackage = MutableStateFlow<NoteCreditPackage?>(null)
    val selectedNotePackage: StateFlow<NoteCreditPackage?> = _selectedNotePackage.asStateFlow()

    private val _selectedQaPackage = MutableStateFlow<QaCreditPackage?>(null)
    val selectedQaPackage: StateFlow<QaCreditPackage?> = _selectedQaPackage.asStateFlow()

    private val _isPlayBillingModalOpen = MutableStateFlow(false)
    val isPlayBillingModalOpen: StateFlow<Boolean> = _isPlayBillingModalOpen.asStateFlow()

    // Global Snackbar Message
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    init {
        viewModelScope.launch {
            entitlementRepository.ensureEntitlementsSeeded()
            schoolRepository.ensureDataSeeded()
            notesRepository.ensureDataSeeded()
            qaRepository.ensureDataSeeded()
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (_screenStack.value.lastOrNull() != screen) {
            _screenStack.value = _screenStack.value + screen
        }
    }

    fun navigateBack(): Boolean {
        if (_screenStack.value.size > 1) {
            _screenStack.value = _screenStack.value.dropLast(1)
            return true
        }
        return false
    }

    /**
     * Updates the search query and automatically triggers live Google Search & Google Suggest
     * after a short typing debounce.
     */
    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        googleSearchJob?.cancel()

        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            _isSearchingGoogle.value = false
            _googleSuggestions.value = emptyList()
            _googleSearchResults.value = emptyList()
            return
        }

        googleSearchJob = viewModelScope.launch {
            // Fetch live Google autocomplete suggestions right away
            if (trimmed.length >= 2) {
                val suggestions = schoolRepository.fetchGoogleSuggestions(trimmed)
                _googleSuggestions.value = suggestions
            }

            // Debounce full Google Search execution while user types
            if (trimmed.length >= 3) {
                delay(400)
                executeGoogleSearchInternal(trimmed, autoConsumeTopResult = false)
            }
        }
    }

    /**
     * Immediately executes a live Google Search for the given school name or EIIN from any search bar
     * (Home screen or School Search screen) and navigates to the Search screen.
     */
    fun searchSchoolOnGoogleNow(query: String) {
        val trimmed = query.trim()
        _searchQuery.value = trimmed
        if (currentScreen.value != AppScreen.SEARCH) {
            navigateTo(AppScreen.SEARCH)
        }
        if (trimmed.isBlank()) return

        // Save in recent searches
        val updatedRecent = listOf(trimmed) + _recentGoogleSearches.value.filterNot { it.equals(trimmed, ignoreCase = true) }
        _recentGoogleSearches.value = updatedRecent.take(8)

        googleSearchJob?.cancel()
        googleSearchJob = viewModelScope.launch {
            executeGoogleSearchInternal(trimmed, autoConsumeTopResult = true)
        }
    }

    private suspend fun executeGoogleSearchInternal(trimmedQuery: String, autoConsumeTopResult: Boolean) {
        _isSearchingGoogle.value = true
        _activeGoogleWebQuery.value = trimmedQuery
        try {
            val liveResults = schoolRepository.searchGoogleLive(trimmedQuery)
            if (liveResults.isNotEmpty()) {
                _googleSearchResults.value = liveResults
                if (autoConsumeTopResult && entitlements.value.schoolCreditsRemaining > 0) {
                    schoolRepository.unlockSchool(liveResults.first())
                }
            } else if (autoConsumeTopResult && entitlements.value.schoolCreditsRemaining > 0) {
                // If local cache matched a school for this explicit search, auto-unlock the top match
                val topCached = searchResults.value.firstOrNull()
                if (topCached != null) {
                    schoolRepository.unlockSchool(topCached)
                }
            }
        } finally {
            _isSearchingGoogle.value = false
        }
    }

    /**
     * Receives live Google Search DOM results extracted from the Android WebView and merges them
     * directly into the search bar results list.
     */
    fun onGoogleWebViewResultsExtracted(query: String, jsonArrayStr: String) {
        if (query.isBlank() || jsonArrayStr.isBlank()) return
        viewModelScope.launch {
            val extracted = schoolRepository.parseAndCacheWebViewResults(query, jsonArrayStr)
            if (extracted.isNotEmpty()) {
                val merged = (extracted + _googleSearchResults.value).distinctBy {
                    if (it.eiin.length == 6 && it.eiin.all { c -> c.isDigit() }) it.eiin else it.name.lowercase()
                }
                _googleSearchResults.value = merged
                if (entitlements.value.schoolCreditsRemaining > 0) {
                    schoolRepository.unlockSchool(extracted.first())
                }
            }
        }
    }

    fun onSelectDivision(division: String) {
        _selectedDivision.value = division
    }

    fun onSelectType(type: String) {
        _selectedType.value = type
    }

    fun onSelectSchool(school: SchoolEntity?) {
        _selectedSchool.value = school
        if (school != null) {
            viewModelScope.launch {
                val isAlreadyUnlocked = unlockedSchoolIds.value.contains(school.id) ||
                    unlockedSchoolIds.value.contains("eiin_${school.eiin}")
                if (!isAlreadyUnlocked && entitlements.value.schoolCreditsRemaining > 0) {
                    schoolRepository.unlockSchool(school)
                }
            }
        }
    }

    fun onUnlockSchool(school: SchoolEntity) {
        viewModelScope.launch {
            val result = schoolRepository.unlockSchool(school)
            result.onSuccess {
                _toastMessage.value = "EIIN Record for '${school.name}' unlocked!"
            }.onFailure { err ->
                _toastMessage.value = err.message ?: "Credit limit reached. Please purchase a credit package."
                _billingTab.value = "SCHOOLS"
                navigateTo(AppScreen.BILLING)
            }
        }
    }

    fun copyEiinToClipboard(eiin: String, schoolName: String, school: SchoolEntity? = null) {
        viewModelScope.launch {
            if (school != null) {
                val isAlreadyUnlocked = unlockedSchoolIds.value.contains(school.id) ||
                    unlockedSchoolIds.value.contains("eiin_${school.eiin}")
                if (!isAlreadyUnlocked) {
                    if (entitlements.value.schoolCreditsRemaining > 0) {
                        schoolRepository.unlockSchool(school)
                    } else {
                        _toastMessage.value = "Credit limit reached (0 credits left). Please purchase a credit package."
                        _billingTab.value = "SCHOOLS"
                        navigateTo(AppScreen.BILLING)
                        return@launch
                    }
                }
            }
            val clipboard = getApplication<Application>().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("School EIIN", eiin)
            clipboard.setPrimaryClip(clip)
            _toastMessage.value = "EIIN: $eiin copied to clipboard!"
        }
    }

    fun onNotesSearchChanged(query: String) {
        _notesSearchQuery.value = query
    }

    fun onSelectSubject(subject: String) {
        _selectedSubject.value = subject
    }

    fun onSelectLevel(level: String) {
        _selectedLevel.value = level
    }

    fun onSelectNote(note: ClassNoteEntity?) {
        _selectedNote.value = note
        _isViewingFullNote.value = false
    }

    fun onUnlockNote(note: ClassNoteEntity) {
        viewModelScope.launch {
            val result = notesRepository.unlockNote(note.id)
            result.onSuccess {
                _toastMessage.value = "Note '${note.title}' unlocked! You have full access."
                _isViewingFullNote.value = true
            }.onFailure { err ->
                _toastMessage.value = err.message ?: "Failed to unlock note"
                _billingTab.value = "NOTES"
                navigateTo(AppScreen.BILLING)
            }
        }
    }

    fun closeNoteReader() {
        _isViewingFullNote.value = false
        _selectedNote.value = null
    }

    // Q&A Operations
    fun onQaSearchChanged(query: String) {
        _qaSearchQuery.value = query
    }

    fun onSelectQaCategory(category: String) {
        _selectedQaCategory.value = category
    }

    fun onSelectQaLevel(level: String) {
        _selectedQaLevel.value = level
    }

    fun onSelectQa(qa: QuestionAnswerEntity?) {
        _selectedQa.value = qa
    }

    fun onUnlockQa(qa: QuestionAnswerEntity) {
        viewModelScope.launch {
            val result = qaRepository.unlockQa(qa.id)
            result.onSuccess {
                _toastMessage.value = "Answer for '${qa.questionTitle.take(35)}...' unlocked!"
                _selectedQa.value = qa
            }.onFailure { err ->
                _toastMessage.value = err.message ?: "Failed to unlock answer"
                _billingTab.value = "QA"
                navigateTo(AppScreen.BILLING)
            }
        }
    }

    fun onAskNewQuestion(
        questionTitle: String,
        questionDetails: String,
        category: String,
        academicLevel: String
    ) {
        viewModelScope.launch {
            val result = qaRepository.askNewQuestion(
                questionTitle = questionTitle,
                questionDetails = questionDetails,
                category = category,
                academicLevel = academicLevel
            )
            result.onSuccess { newQa ->
                _toastMessage.value = "Question submitted (1 Q&A Credit used) & solution unlocked!"
                _selectedQa.value = newQa
            }.onFailure { err ->
                _toastMessage.value = err.message ?: "No Q&A credits remaining"
                _billingTab.value = "QA"
                navigateTo(AppScreen.BILLING)
            }
        }
    }

    fun setBillingTab(tab: String) {
        _billingTab.value = tab
    }

    fun initiateSchoolPurchase(pkg: SchoolCreditPackage, activity: Activity? = null) {
        _selectedSchoolPackage.value = pkg
        _selectedNotePackage.value = null
        _selectedQaPackage.value = null
        if (!playBillingHelper.launchGooglePlayFlowIfConfigured(activity, pkg.sku)) {
            _isPlayBillingModalOpen.value = true
        }
    }

    fun initiateNotePurchase(pkg: NoteCreditPackage, activity: Activity? = null) {
        _selectedNotePackage.value = pkg
        _selectedSchoolPackage.value = null
        _selectedQaPackage.value = null
        if (!playBillingHelper.launchGooglePlayFlowIfConfigured(activity, pkg.sku)) {
            _isPlayBillingModalOpen.value = true
        }
    }

    fun initiateQaPurchase(pkg: QaCreditPackage, activity: Activity? = null) {
        _selectedQaPackage.value = pkg
        _selectedSchoolPackage.value = null
        _selectedNotePackage.value = null
        if (!playBillingHelper.launchGooglePlayFlowIfConfigured(activity, pkg.sku)) {
            _isPlayBillingModalOpen.value = true
        }
    }

    fun dismissBillingModal() {
        _isPlayBillingModalOpen.value = false
        _selectedSchoolPackage.value = null
        _selectedNotePackage.value = null
        _selectedQaPackage.value = null
    }

    private fun handleNativePlayPurchase(sku: String, purchaseToken: String, orderId: String) {
        viewModelScope.launch {
            val schoolPkg = PricingCatalog.schoolPackages.find { it.sku == sku }
            val notePkg = PricingCatalog.notePackages.find { it.sku == sku }
            val qaPkg = PricingCatalog.qaPackages.find { it.sku == sku }

            val (productType, qty, price) = when {
                schoolPkg != null -> Triple("SCHOOL_CREDITS", schoolPkg.schoolRecords, schoolPkg.priceUsd)
                notePkg != null -> Triple("NOTE_CREDITS", notePkg.noteCount, notePkg.priceUsd)
                qaPkg != null -> Triple("QA_CREDITS", qaPkg.questionCount, qaPkg.priceUsd)
                else -> return@launch
            }

            val result = billingRepository.processPurchase(
                productType = productType,
                sku = sku,
                quantity = qty,
                priceUsd = price,
                simulatedOutcome = "SUCCESS",
                explicitOrderId = orderId,
                explicitPurchaseToken = purchaseToken
            )
            if (result is PurchaseResult.Success) {
                _toastMessage.value = "Google Play Purchase Verified! $qty credits granted."
            }
        }
    }

    fun completePurchaseWithOutcome(simulatedOutcome: String) {
        viewModelScope.launch {
            val schoolPkg = _selectedSchoolPackage.value
            val notePkg = _selectedNotePackage.value
            val qaPkg = _selectedQaPackage.value

            val (productType, sku, qty, price) = when {
                schoolPkg != null -> Quad(
                    "SCHOOL_CREDITS",
                    schoolPkg.sku,
                    schoolPkg.schoolRecords,
                    schoolPkg.priceUsd
                )
                notePkg != null -> Quad(
                    "NOTE_CREDITS",
                    notePkg.sku,
                    notePkg.noteCount,
                    notePkg.priceUsd
                )
                qaPkg != null -> Quad(
                    "QA_CREDITS",
                    qaPkg.sku,
                    qaPkg.questionCount,
                    qaPkg.priceUsd
                )
                else -> return@launch
            }

            val result = billingRepository.processPurchase(
                productType = productType,
                sku = sku,
                quantity = qty,
                priceUsd = price,
                simulatedOutcome = simulatedOutcome
            )

            when (result) {
                is PurchaseResult.Success -> {
                    _toastMessage.value = "Google Play Billing Success! $qty credits added to your account."
                    dismissBillingModal()
                }
                is PurchaseResult.Pending -> {
                    _toastMessage.value = "Google Play Order Pending: Awaiting Play Store confirmation."
                    dismissBillingModal()
                }
                is PurchaseResult.UserCancelled -> {
                    _toastMessage.value = "Google Play payment cancelled."
                    dismissBillingModal()
                }
                is PurchaseResult.Error -> {
                    _toastMessage.value = result.errorMessage
                }
            }
        }
    }

    fun restorePurchases() {
        viewModelScope.launch {
            playBillingHelper.restoreGooglePlayPurchases()
            billingRepository.restorePurchases()
            _toastMessage.value = "Purchases and entitlements synchronized with Google Play Store!"
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    // Admin Operations
    fun adminAddSchool(school: SchoolEntity) {
        viewModelScope.launch {
            schoolRepository.insertOrUpdateSchool(school)
            _toastMessage.value = "School record '${school.name}' added with EIIN ${school.eiin}!"
        }
    }

    fun adminDeleteSchool(id: String) {
        viewModelScope.launch {
            schoolRepository.deleteSchool(id)
            _toastMessage.value = "School record deleted."
        }
    }

    fun adminAddNote(note: ClassNoteEntity) {
        viewModelScope.launch {
            notesRepository.insertOrUpdateNote(note)
            _toastMessage.value = "Class Note '${note.title}' added to marketplace!"
        }
    }

    fun adminResetDefaults() {
        viewModelScope.launch {
            entitlementRepository.resetAllForTesting()
            schoolRepository.ensureDataSeeded()
            notesRepository.ensureDataSeeded()
            qaRepository.ensureDataSeeded()
            _toastMessage.value = "Reset sample data and entitlements to default state."
        }
    }

    private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
}
