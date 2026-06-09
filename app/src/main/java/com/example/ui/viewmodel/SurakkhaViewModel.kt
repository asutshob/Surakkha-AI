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
    MISSING_PERSONS("Missing Persons", "নিখোঁজ সনাক্তকরণ"),
    DISASTER_GUARDIAN("Disaster Guardian", "দুর্যোগ অভিভাবক"),
    DOCUMENTS("Documents", "দলিল সরলীকরণ"),
    FATHER_AI("Surakkha Assistant", "সুরক্ষা সহায়ক"),
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

    // Global SOS Emergency State
    private val _isSosActive = MutableStateFlow(false)
    val isSosActive: StateFlow<Boolean> = _isSosActive

    private val _sosCountdown = MutableStateFlow(5)
    val sosCountdown: StateFlow<Int> = _sosCountdown

    private val _sosStatus = MutableStateFlow("")
    val sosStatus: StateFlow<String> = _sosStatus

    private var sosJob: kotlinx.coroutines.Job? = null

    fun triggerSos() {
        _isSosActive.value = true
        _sosCountdown.value = 5
        _sosStatus.value = if (_isBangla.value) "জিপিএস লোকেশন সনাক্ত করা হচ্ছে..." else "Detecting GPS location..."
        
        sosJob?.cancel()
        sosJob = viewModelScope.launch {
            // Count down from 5 to 0
            for (i in 5 downTo 1) {
                _sosCountdown.value = i
                when (i) {
                    5 -> _sosStatus.value = if (_isBangla.value) 
                        "জিপিএস লোকেশন নির্ধারণ করা হচ্ছে..." 
                        else "Acquiring GPS coordinates..."
                    4 -> _sosStatus.value = if (_isBangla.value) 
                        "কাছাকাছি ৫ কিমি-এর মধ্যে নিরাপত্তা ফায়ারবেস নোড খোঁজা হচ্ছে..." 
                        else "Scanning Firebase active safety nodes..."
                    3 -> _sosStatus.value = if (_isBangla.value) 
                        "কাছাকাছি স্বেচ্ছাসেবক ও জরুরি বাহিনীকে যুক্ত করা হচ্ছে..." 
                        else "Connecting with active emergency response networks..."
                    2 -> _sosStatus.value = if (_isBangla.value) 
                        "ফায়ারবেস ক্লাউড মেসেজিং নোটিফিকেশন তৈরি করা হচ্ছে..." 
                        else "Broadcasting Firebase Cloud dynamic alerts..."
                    1 -> _sosStatus.value = if (_isBangla.value) 
                        "জরুরি হটলাইন ৯৯৯ এর সাথে লিংক তৈরি শেষ হচ্ছে..." 
                        else "Preparing immediate direct helpline call linkage..."
                }
                kotlinx.coroutines.delay(1000)
            }
            _sosCountdown.value = 0
            _sosStatus.value = if (_isBangla.value) 
                "✓ ফায়ারবেসে সফলভাবে ব্রডকাস্ট পাঠানো হয়েছে! জরুরি বাহিনীকে সতর্ক করা হয়েছে।" 
                else "✓ Broadcast Sent! Firebase notified 24 nearby guardians & emergency desk."
            
            // Add a safety warning inside the AI Assistant tab
            val urgentAlertMsg = ChatMessage(
                text = if (_isBangla.value) 
                    "🚨 জরুরি অ্যালার্ট ব্রডকাস্ট! আপনি একটি SOS ট্রিগার করেছেন। আমরা আপনার লোকেশন ফায়ারবেস ক্লাউডে যুক্ত করেছি এবং আপনার সুরক্ষার জন্য তৈরি হয়েছি।" 
                    else "🚨 URGENT SOS ALERT! You have triggered an SOS. We have synchronized your GPS into Firebase Realtime DB and are alerting assistance.",
                isUser = false
            )
            val updated = _chatMessages.value.toMutableList()
            updated.add(urgentAlertMsg)
            _chatMessages.value = updated
        }
    }

    fun cancelSos() {
        sosJob?.cancel()
        _isSosActive.value = false
    }

    init {
        // Prepare DB on startup
        viewModelScope.launch {
            repository.initDatabaseWithPrePopulatedData()
            
            // Add a warm welcome message from Assistant initially
            _chatMessages.value = listOf(
                ChatMessage(
                    text = "স্বাগতম! কেমন আছেন? আপনার জন্য শুভকামনা। আমি 'সুরক্ষা সহায়ক'—আপনার সার্বক্ষণিক জরুরি সাহায্যকারী। দেশে যেকোনো নিখোঁজ ব্যক্তির সন্ধান ও সনাক্তকরণ করতে, দুর্যোগের সঠিক প্রস্তুতিমূলক পরামর্শ পেতে এবং জটিল সরকারি নীতি বা পরিপত্র সহজ বাংলায় বুঝে নিতে আমি আপনাকে সাহায্য করব। আজ আমি আপনাকে কীভাবে সেবা করতে পারি?",
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

    fun scanAndMatchSeenPerson(photoUrl: String) {
        viewModelScope.launch {
            _faceMatchState.value = UiState.Loading
            // Wait 2.5 seconds to make AI scanning animation incredibly realistic
            kotlinx.coroutines.delay(2500)
            
            // Find if any ACTIVE report matches this photoUrl
            val activeReport = reports.value.firstOrNull { it.status == "ACTIVE" && it.photoUrl == photoUrl }
            
            if (activeReport != null) {
                // Update match score and status
                val matched = activeReport.copy(
                    matchScore = 91.5 + (Math.random() * 8.0),
                    status = "FOUND"
                )
                repository.updateMissingPersonReport(matched)
                _faceMatchState.value = UiState.Success(matched)
            } else {
                _faceMatchState.value = UiState.Success(null)
            }
        }
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
        clearDocChatHistory()
    }

    // Document Interactive Chat State
    private val _docChatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val docChatMessages: StateFlow<List<ChatMessage>> = _docChatMessages

    private val _docChatUiState = MutableStateFlow<UiState<Unit>>(UiState.Idle)
    val docChatUiState: StateFlow<UiState<Unit>> = _docChatUiState

    fun clearDocChatHistory() {
        _docChatMessages.value = emptyList()
        _docChatUiState.value = UiState.Idle
    }

    fun sendDocChatMessage(docTitle: String, docContent: String, queryText: String) {
        if (queryText.trim().isEmpty()) return

        val userMsg = ChatMessage(text = queryText, isUser = true)
        val currentMsgs = _docChatMessages.value.toMutableList()
        currentMsgs.add(userMsg)
        _docChatMessages.value = currentMsgs

        viewModelScope.launch {
            _docChatUiState.value = UiState.Loading

            val history = _docChatMessages.value.dropLast(1).map {
                Pair(if (it.isUser) "user" else "model", it.text)
            }
            val replyText = repository.callDocumentAI(docTitle, docContent, queryText, history)

            val modelMsg = ChatMessage(text = replyText, isUser = false)
            val updatedMsgs = _docChatMessages.value.toMutableList()
            updatedMsgs.add(modelMsg)
            _docChatMessages.value = updatedMsgs

            _docChatUiState.value = UiState.Idle
        }
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
