package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AnalyticsRecord
import com.example.data.database.DisasterChecklist
import com.example.data.database.MissingPersonReport
import com.example.data.database.SimplifiedDocument
import com.example.data.repository.SurakkhaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val titleEn: String, val titleBn: String) {
    HOME("Home", "হোম"),
    MISSING_PERSONS("Missing Persons", "নিখোঁজ সন্ধান"),
    DISASTER_GUARDIAN("Disaster Guardian", "দুর্যোগ অভিভাবক"),
    DOCUMENTS("Documents", "দলিল সরলীকরণ"),
    FATHER_AI("Father AI", "ফাদার এআই"),
    ANALYTICS("Impact Tracker", "প্রভাব ড্যাশবোর্ড")
}

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

sealed interface UiState<out T> {
    object Idle : UiState<Nothing>
    object Loading : UiState<Nothing>
    data class Success<out T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}

class SurakkhaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = SurakkhaRepository(application)

    // Current Navigation Tab
    private val _currentTab = MutableStateFlow(AppTab.HOME)
    val currentTab: StateFlow<AppTab> = _currentTab

    // Language Settings (True = Bangla, False = English)
    private val _isBangla = MutableStateFlow(true)
    val isBangla: StateFlow<Boolean> = _isBangla

    // Database Flows
    val reports: StateFlow<List<MissingPersonReport>> = repository.allReports
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val checklistItems: StateFlow<List<DisasterChecklist>> = repository.allChecklistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val simplifiedDocuments: StateFlow<List<SimplifiedDocument>> = repository.allSimplifiedDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val analytics: StateFlow<AnalyticsRecord?> = repository.analyticsRecord
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Father AI Chat State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages

    private val _chatUiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val chatUiState: StateFlow<UiState<Unit>> = _chatUiState

    // Document Simplifier State
    private val _documentUiState = MutableStateFlow<UiState<SimplifiedDocument>>(UiState.Idle)
    val documentUiState: StateFlow<UiState<SimplifiedDocument>> = _documentUiState

    // Disaster Guardian Call State
    private val _disasterAlertState = MutableStateFlow<UiState<String>>(UiState.Idle)
    val disasterAlertState: StateFlow<UiState<String>> = _disasterAlertState

    // Face Match simulation state
    private val _faceMatchState = MutableStateFlow<UiState<MissingPersonReport?>>(UiState.Idle)
    val faceMatchState: StateFlow<UiState<MissingPersonReport?>> = _faceMatchState

    init {
        // Prepare DB on startup
        viewModelScope.launch {
            repository.initDatabaseWithPrePopulatedData()
            
            // Add a warm welcome message from Father AI initially
            _chatMessages.value = listOf(
                ChatMessage(
                    text = "স্বাগতম! কেমন আছেন? আপনার জন্য শুভকামনা। আমি 'ফাদার এআই'—সুরক্ষার সেন্ট্রাল অ্যাসিস্ট্যান্ট। দেশে যেকোনো নিখোঁজ ব্যক্তির সন্ধান করতে, দুর্যোগ প্রস্তুতিমূলক পরামর্শ দিতে এবং জটিল সরকারি নীতি পরিপত্র সরল বাংলায় বুঝে নিতে সাহায্য করতে পারি। আজ আপনাকে কীভাবে সেবা করতে পারি?",
                    isUser = false
                )
            )
        }
    }

    // Navigation and Language setters
    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun toggleLanguage() {
        _isBangla.value = !_isBangla.value
    }

    // --- 1. Missing Persons actions ---
    fun submitMissingPersonReport(
        name: String,
        age: Int,
        location: String,
        description: String,
        contact: String,
        reporter: String,
        avatarIndex: String
    ) {
        viewModelScope.launch {
            val report = MissingPersonReport(
                name = name,
                age = age,
                lastSeenLocation = location,
                description = description,
                contactPhone = contact,
                reporterName = reporter,
                photoUrl = avatarIndex,
                dateReported = "আজ, ০৯ জুন ২০২৬",
                status = "ACTIVE",
                isRegionalAlertSent = true
            )
            val newId = repository.addMissingPersonReport(report)
            simulateFaceMatch(report.copy(id = newId.toInt()))
        }
    }

    private fun simulateFaceMatch(submittedReport: MissingPersonReport) {
        viewModelScope.launch {
            _faceMatchState.value = UiState.Loading
            // Wait 2.5 seconds to make AI scanning animation incredibly realistic
            kotlinx.coroutines.delay(2500)
            
            // Simulate that in Bangladesh’s community tracking, we matching matching elements:
            // 20% chance of high match score (finding a potential child match on camera feed near another location)
            val isSuccess = Math.random() > 0.4
            if (isSuccess) {
                // Return a simulation match report updated in DB
                val updatedReport = submittedReport.copy(
                    matchScore = 85.0 + (Math.random() * 14.0),
                    status = "FOUND"
                )
                repository.updateMissingPersonReport(updatedReport)
                _faceMatchState.value = UiState.Success(updatedReport)
            } else {
                _faceMatchState.value = UiState.Success(null)
            }
        }
    }

    fun clearFaceMatchStatus() {
        _faceMatchState.value = UiState.Idle
    }

    fun updateReportStatus(report: MissingPersonReport, newStatus: String) {
        viewModelScope.launch {
            val updated = report.copy(status = newStatus)
            repository.updateMissingPersonReport(updated)
        }
    }

    // --- 2. Disaster Guardian actions ---
    fun loadDisasterAlert(type: String, location: String) {
        viewModelScope.launch {
            _disasterAlertState.value = UiState.Loading
            val alert = repository.getDisasterAlertAndSafety(type, location)
            _disasterAlertState.value = UiState.Success(alert)
        }
    }

    fun toggleChecklistItem(item: DisasterChecklist) {
        viewModelScope.launch {
            val updated = item.copy(isChecked = !item.isChecked)
            repository.updateChecklistItem(updated)
        }
    }

    fun addNewChecklistItem(text: String, category: String) {
        viewModelScope.launch {
            val item = DisasterChecklist(
                itemText = text,
                isChecked = false,
                category = category
            )
            repository.addChecklistItem(item)
        }
    }

    fun deleteChecklistItem(item: DisasterChecklist) {
        viewModelScope.launch {
            repository.deleteChecklistItem(item)
        }
    }

    // --- 3. Document Simplifier actions ---
    fun simplifyNewDocument(title: String, rawContent: String) {
        viewModelScope.launch {
            _documentUiState.value = UiState.Loading
            val simplified = repository.simplifyDocument(title, rawContent)
            repository.saveSimplifiedDocument(simplified)
            _documentUiState.value = UiState.Success(simplified)
        }
    }

    fun deleteDocument(doc: SimplifiedDocument) {
        viewModelScope.launch {
            repository.deleteSimplifiedDocument(doc)
        }
    }

    fun clearSimplifiedDocState() {
        _documentUiState.value = UiState.Idle
    }

    // --- 4. Father AI Actions ---
    fun sendChatMessage(text: String) {
        if (text.trim().isEmpty()) return
        
        val userMsg = ChatMessage(text = text, isUser = true)
        val currentMsgs = _chatMessages.value.toMutableList()
        currentMsgs.add(userMsg)
        _chatMessages.value = currentMsgs
        
        viewModelScope.launch {
            _chatUiState.value = UiState.Loading
            
            // Map the dialogue structures to the repository call
            val history = _chatMessages.value.dropLast(1).map { 
                Pair(if (it.isUser) "user" else "model", it.text)
            }
            val replyText = repository.callFatherAI(text, history)
            
            val modelMsg = ChatMessage(text = replyText, isUser = false)
            val updatedMsgs = _chatMessages.value.toMutableList()
            updatedMsgs.add(modelMsg)
            _chatMessages.value = updatedMsgs
            
            _chatUiState.value = UiState.Idle
        }
    }

    // --- 5. Impact & Analytics Dashboard specific actions ---
    fun registerNewVolunteer() {
        viewModelScope.launch {
            repository.registerVolunteer()
        }
    }
}
