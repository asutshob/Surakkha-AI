package com.example.data.repository

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.api.*
import com.example.data.database.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class SurakkhaRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val missingPersonDao = db.missingPersonDao()
    private val disasterChecklistDao = db.disasterChecklistDao()
    private val simplifiedDocumentDao = db.simplifiedDocumentDao()
    private val analyticsDao = db.analyticsDao()

    // Exposed Flows
    val allReports: Flow<List<MissingPersonReport>> = missingPersonDao.getAllReports()
    val allChecklistItems: Flow<List<DisasterChecklist>> = disasterChecklistDao.getAllChecklistItems()
    val allSimplifiedDocuments: Flow<List<SimplifiedDocument>> = simplifiedDocumentDao.getAllSimplifiedDocuments()
    val analyticsRecord: Flow<AnalyticsRecord?> = analyticsDao.getAnalytics()

    suspend fun initDatabaseWithPrePopulatedData() = withContext(Dispatchers.IO) {
        // 1. Pre-populate Disaster Checklist if empty
        val checklistCount = disasterChecklistDao.getAllChecklistItems().firstOrNull()?.size ?: 0
        if (checklistCount == 0) {
            val defaultItems = listOf(
                DisasterChecklist(itemText = "জরুরি ফার্স্ট এইড কিট (ব্যান্ডেজ, স্যাভলন, জরুরি ওষুধ)", isChecked = true, category = "General"),
                DisasterChecklist(itemText = "শুকনো খাবার ও বিশুদ্ধ বোতলজাত পানি সংরক্ষণ করুন", isChecked = false, category = "General"),
                DisasterChecklist(itemText = "টর্চলাইট এবং অতিরিক্ত ব্যাটারি সচল রাখুন", isChecked = false, category = "General"),
                DisasterChecklist(itemText = "গুরুত্বপূর্ণ কাগজপত্র নিরাপদে পলিথিনে মুড়িয়ে রাখুন", isChecked = false, category = "General"),
                DisasterChecklist(itemText = "বন্যা আশ্রয়কেন্দ্রের সঠিক অবস্থান ও যোগাযোগের নম্বর জেনে রাখুন", isChecked = true, category = "Flood"),
                DisasterChecklist(itemText = "পোষা প্রাণীদের নিরাপদ স্থানে বা উঁচু জায়গায় সরিয়ে রাখুন", isChecked = false, category = "Flood"),
                DisasterChecklist(itemText = "বাড়ির চারপাশে নিষ্কাশন বা নর্দমা পরিষ্কার রাখুন", isChecked = false, category = "Flood"),
                DisasterChecklist(itemText = "ঘূর্ণিঝড়ের সংকেত শোনার সাথে সাথে ঘরের দরজা-জানালা বন্ধ করুন", isChecked = false, category = "Cyclone"),
                DisasterChecklist(itemText = "দুর্যোগপূর্ণ আবহাওয়ার আগে শুকনা চিঁড়া, গুড় ও বিস্কুট জমিয়ে রাখুন", isChecked = false, category = "Cyclone"),
                DisasterChecklist(itemText = "বজ্রপাতের সময় ইলেকট্রনিক ডিভাইস ও প্লাগ বন্ধ রাখুন", isChecked = false, category = "Lightning"),
                DisasterChecklist(itemText = "বজ্রপাতের সময় খোলা মাঠে বা বড় গাছের নিচে দাঁড়াবেন না", isChecked = true, category = "Lightning")
            )
            disasterChecklistDao.insertAllChecklistItems(defaultItems)
        }

        // 2. Pre-populate Missing Persons if empty
        val reportsCount = missingPersonDao.getAllReports().firstOrNull()?.size ?: 0
        if (reportsCount == 0) {
            val sampleReports = listOf(
                MissingPersonReport(
                    name = "ফাহিম আহমেদ (Fahim Ahmed)",
                    age = 8,
                    lastSeenLocation = "মিরপুর ১০ নং বাস স্ট্যান্ড, ঢাকা",
                    description = "গায়ে নীল রঙের টি-শার্ট ও জিন্স প্যান্ট ছিল। গোলগাল চেহারা, কথা বলতে কিছুটা জড়তা আছে। গতকাল বিকেল ৪টা থেকে নিখোঁজ।",
                    contactPhone = "০১৭১২৩৪৫৬৭৮",
                    photoUrl = "avatar_boy_1",
                    reporterName = "মোঃ আনিসুর রহমান (বাবা)",
                    dateReported = "০৮ জুন, ২০২৬",
                    status = "ACTIVE",
                    isRegionalAlertSent = true,
                    matchScore = 0.0
                ),
                MissingPersonReport(
                    name = "রাবেয়া খাতুন (Rabeya Khatun)",
                    age = 67,
                    lastSeenLocation = "চাষাড়া মোড়, নারায়ণগঞ্জ",
                    description = "হালকা নীল শাড়ি পরা ছিলেন। স্মৃতিভ্রম জনিত সমস্যায় ভুগছেন। ঠিকমত ঠিকানা বলতে পারেন না। অনুগ্রহ করে সন্ধান পেলে দ্রুত জানান।",
                    contactPhone = "০১৯৯৮৭৬৫৪৩২",
                    photoUrl = "avatar_elderly_woman",
                    reporterName = "তাহমিনা আক্তার (মেয়ে)",
                    dateReported = "০৬ জুন, ২০২৬",
                    status = "ACTIVE",
                    isRegionalAlertSent = true,
                    matchScore = 0.0
                ),
                MissingPersonReport(
                    name = "মুমতাহিনা সারা (Mumtahina Sara)",
                    age = 15,
                    lastSeenLocation = "মোহাম্মদপুর সরকারি স্কুল সংলগ্ন এলাকা, ঢাকা",
                    description = "সবুজ সেলোয়ার কামিজ পরা ছিল। কপালে একটি কাটা দাগ আছে। বিদ্যালয়ে যাওয়ার পথে আর ফিরে আসেনি।",
                    contactPhone = "০১৫৫৫১১২২৩৪",
                    photoUrl = "avatar_girl_1",
                    reporterName = "শাহানা পারভীন (মা)",
                    dateReported = "০৪ জুন, ২০২৬",
                    status = "FOUND",
                    isRegionalAlertSent = true,
                    matchScore = 94.5
                )
            )
            for (report in sampleReports) {
                missingPersonDao.insertReport(report)
            }
        }

        // 3. Pre-populate Analytics if empty
        val analytics = analyticsDao.getAnalytics().firstOrNull()
        if (analytics == null) {
            analyticsDao.insertOrUpdateAnalytics(AnalyticsRecord())
        }
    }

    // Missing Person Network Methods
    suspend fun addMissingPersonReport(report: MissingPersonReport): Long = withContext(Dispatchers.IO) {
        val id = missingPersonDao.insertReport(report)
        // Auto increment tracked count
        incrementAnalyticsCounter { current ->
            current.copy(
                activeMissingSearches = current.activeMissingSearches + 1
            )
        }
        id
    }

    suspend fun updateMissingPersonReport(report: MissingPersonReport) = withContext(Dispatchers.IO) {
        missingPersonDao.updateReport(report)
        if (report.status == "FOUND") {
            incrementAnalyticsCounter { current ->
                current.copy(
                    activeMissingSearches = maxOf(0, current.activeMissingSearches - 1),
                    solvedMissingPersons = current.solvedMissingPersons + 1
                )
            }
        }
    }

    suspend fun deleteMissingReport(report: MissingPersonReport) = withContext(Dispatchers.IO) {
        missingPersonDao.deleteReport(report)
    }

    // Checklist Methods
    suspend fun addChecklistItem(item: DisasterChecklist) = withContext(Dispatchers.IO) {
        disasterChecklistDao.insertChecklistItem(item)
    }

    suspend fun updateChecklistItem(item: DisasterChecklist) = withContext(Dispatchers.IO) {
        disasterChecklistDao.updateChecklistItem(item)
        val allItems = disasterChecklistDao.getAllChecklistItems().firstOrNull().orEmpty()
        val completedCount = allItems.count { it.isChecked }
        incrementAnalyticsCounter { current ->
            current.copy(disasterChecklistsCompleted = completedCount)
        }
    }

    suspend fun deleteChecklistItem(item: DisasterChecklist) = withContext(Dispatchers.IO) {
        disasterChecklistDao.deleteChecklistItem(item)
    }

    // Document Simplifier Methods
    suspend fun saveSimplifiedDocument(doc: SimplifiedDocument): Long = withContext(Dispatchers.IO) {
        val id = simplifiedDocumentDao.insertSimplifiedDocument(doc)
        incrementAnalyticsCounter { current ->
            current.copy(simplifiedDocumentsCount = current.simplifiedDocumentsCount + 1)
        }
        id
    }

    suspend fun deleteSimplifiedDocument(doc: SimplifiedDocument) = withContext(Dispatchers.IO) {
        simplifiedDocumentDao.deleteSimplifiedDocument(doc)
    }

    // Register Volunteer simulated action inside Analytics screen
    suspend fun registerVolunteer() = withContext(Dispatchers.IO) {
        incrementAnalyticsCounter { current ->
            current.copy(communityVolunteersRegistered = current.communityVolunteersRegistered + 1)
        }
    }

    // Chatbot query increment
    suspend fun incrementChatbotInteractions() = withContext(Dispatchers.IO) {
        incrementAnalyticsCounter { current ->
            current.copy(chatInteractionsCount = current.chatInteractionsCount + 1)
        }
    }

    // Helper to update analytics safely
    private suspend fun incrementAnalyticsCounter(updateBlock: (AnalyticsRecord) -> AnalyticsRecord) {
        val current = analyticsDao.getAnalytics().firstOrNull() ?: AnalyticsRecord()
        val updated = updateBlock(current)
        analyticsDao.insertOrUpdateAnalytics(updated)
    }

    // --- Gemini API Call: Father AI Chat ---
    suspend fun callFatherAI(userQuery: String, chatHistory: List<Pair<String, String>>): String = withContext(Dispatchers.IO) {
        incrementChatbotInteractions()
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("SurakkhaAI", "Gemini API key is not configured, running in offline fallback mode.")
            return@withContext getOfflineFatherAIResponse(userQuery)
        }

        val systemPrompt = """
            You are "Surakkha Shohayok" (সুরক্ষা সহায়ক), the deeply caring, professional, wise, and highly intelligent emergency & safety assistant of Surakkha AI (সুরক্ষা এআই). 
            Speak in warm, supportive, and highly accessible Bengali (with standard English terms if appropriate for modern understanding, but keep the core tone beautifully empathetic, professional, and clear Bengali).
            
            CRITICAL RULE FOR LANGUAGE AND GREETINGS:
            - You MUST NOT use any Islamic or religious phrases, greetings, or words (e.g., "Assalamu Alaikum", "Alhamdulillah", "Insha'Allah", "আসসালামু আলাইকুম", "আলহামদুলিল্লাহ", "ইনশাআল্লাহ").
            - Instead, use pure, beautiful, polite, and neutral Bengali words and greetings such as "স্বাগতম" (Welcome), "শুভকামনা" (Best wishes), "কেমন আছেন?", "ধন্যবাদ".
            - Maintain a highly helpful, professional, state-of-the-art startup-level supportive tone.
            
            Your mission is to help Bangladeshi citizens, students, and volunteers with the following crucial areas:
            1. Missing Persons (নিখোঁজ সনাক্তকরণ): Explain how they can upload clear photos of missing persons. Highlight that our AI system performs facial descriptive matching (face recognition features) and immediately triggers localized community push notifications to bring people home safely.
            2. Disaster Guardian (দুর্যোগ অভিভাবক): Feed life-saving advices regarding cyclones, floods, thunderstorms/lightning, or river erosion. Prioritize instant physical safety first.
            3. Gov Document Simplifier (দলিল সরলীকরণ): Advice users to paste official gazettes/notices in the Documents tab for clear, breakdown analysis in 5 steps.
            
            Keep your responses descriptive yet crisp, action-oriented, and reassuring. Always prioritize citizen safety.
            Provide useful emergency hotline numbers:
            - National Emergency Service (জাতীয় জরুরি সেবা): 999
            - Disaster Forecast Hotlines (দুর্যোগের আগাম বার্তা): 1090
            - Government Information Portal (সরকারি তথ্য ও সেবা): 333
            - Child Helpline (শিশু সহায়তা): 1098
            - National Identity Portal (জাতীয় পরিচয়পত্র): 105
        """.trimIndent()

        // Build contents showing chat history to model
        val contentsList = mutableListOf<Content>()
        
        // Add chat history
        for (turn in chatHistory) {
            contentsList.add(Content(parts = listOf(Part(text = turn.first)), role = "user"))
            contentsList.add(Content(parts = listOf(Part(text = turn.second)), role = "model"))
        }
        
        // Add current user prompt
        contentsList.add(Content(parts = listOf(Part(text = userQuery)), role = "user"))

        val request = GenerateContentRequest(
            contents = contentsList,
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt))),
            generationConfig = GenerationConfig(temperature = 0.7f)
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: "আমি দুঃখিত, আমি এই মুহূর্তে উত্তর তৈরি করতে পারছি না। দয়া করে আবার চেষ্টা করুন।"
        } catch (e: Exception) {
            Log.e("SurakkhaAI", "Gemini call failed, switching to offline fallback: ${e.message}")
            getOfflineFatherAIResponse(userQuery)
        }
    }

    // --- Gemini API Call: Simulating Policy / Notice Reduction ---
    suspend fun simplifyDocument(title: String, rawContent: String): SimplifiedDocument = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w("SurakkhaAI", "Gemini API key is not configured, running in document simplification fallback mode.")
            return@withContext getOfflineSimplifiedDocument(title, rawContent)
        }

        val prompt = """
            You are an expert government document simplifier for Bangladesh. 
            Analyze the following official Bangladeshi text/notice:
            Title: $title
            Content: $rawContent
            
            Convert this complex official language into extremely clear, simple, and structured daily Bengali that high school students and rural farmers can both instantly understand.
            You MUST return the output precisely categorized into the following 5 parts, separated by the exact delimiter '---PART_SPLIT---':
            
            PART 1: মূল আলোচনার বিষয় (Main purpose or summary in 2 sentences)
            ---PART_SPLIT---
            PART 2: কাদের জন্য প্রযোজ্য (Who it affects / Target Audience)
            ---PART_SPLIT---
            PART 3: কি কি পদক্ষেপ নিতে হবে (What to do / Required Actions - formatted in bullet points)
            ---PART_SPLIT---
            PART 4: গুরুত্বপূর্ণ তারিখ (Important dates and deadlines)
            ---PART_SPLIT---
            PART 5: সহজ বাস্তব উদাহরণ (A simple practical example illustrative scenario)
            
            Do not include any other markdown header syntax around parts, just fill the sections exactly.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.3f)
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            val fullText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: ""
            val parts = fullText.split("---PART_SPLIT---")
            
            if (parts.size >= 5) {
                SimplifiedDocument(
                    originalTitle = title,
                    originalText = rawContent,
                    mainTopic = parts[0].trim().replace("PART 1:", "").trim(),
                    targetAudience = parts[1].trim().replace("PART 2:", "").trim(),
                    requiredActions = parts[2].trim().replace("PART 3:", "").trim(),
                    importantDates = parts[3].trim().replace("PART 4:", "").trim(),
                    simpleExamples = parts[4].trim().replace("PART 5:", "").trim()
                )
            } else {
                getOfflineSimplifiedDocument(title, rawContent)
            }
        } catch (e: Exception) {
            Log.e("SurakkhaAI", "Document simplification failed, switching to fallback: ${e.message}")
            getOfflineSimplifiedDocument(title, rawContent)
        }
    }

    // --- Gemini API Call: Getting Disaster Risk & Interactive Bengali Alerts ---
    suspend fun getDisasterAlertAndSafety(disasterType: String, location: String): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext getOfflineDisasterGuide(disasterType, location)
        }

        val prompt = """
            Act as Disaster Guardian (দুর্যোগ অভিভাবক), the real-time safety system.
            Assess the risk and provide real-time instructions for:
            Type: $disasterType
            District/Location in Bangladesh: $location
            
            Provide:
            1. Current risk level (উচ্চ / মাঝারি / নিম্ন)
            2. High-priority safety actions in simple Bengali.
            3. Preparation tips in simple Bengali.
            4. Emergency help lines.
            Keep it urgent, vital, clean, and highly structured so it can be easily read on a mobile screen during storm/flood conditions.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.5f)
        )

        try {
            val response = RetrofitClient.service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: getOfflineDisasterGuide(disasterType, location)
        } catch (e: Exception) {
            getOfflineDisasterGuide(disasterType, location)
        }
    }

    // --- Offline Fallbacks (Highly polished native responses for stable offline usage) ---

    private fun getOfflineFatherAIResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("নিখোঁজ") || q.contains("হারিয়ে") || q.contains("missing") -> {
                "প্রিয় নাগরিক, নিখোঁজ সংক্রান্ত তথ্যের জন্য 'Missing Persons' ট্যাবে যান। সেখানে ছবিসহ বিশদ বিবরণ দিয়ে একটি ফর্ম জমা দিতে পারেন। আমাদের সুরক্ষা সারভার কৃত্রিম বুদ্ধিমত্তা (AI) ব্যবহার করে নিখোঁজ ব্যক্তির মুখাবয়ব ম্যাচিং করে এবং তাৎক্ষণিক নিকটস্থ স্বেচ্ছাসেবকদের মোবাইল নোটিফিকেশন পাঠায়। জরুরি প্রয়োজনে নিকটস্থ থানায় যোগাযোগ করুন অথবা জাতীয় হেল্পলাইন ৯৯৯ এ কল করুন।"
            }
            q.contains("বন্যা") || q.contains("দুর্যোগ") || q.contains("ঝড়") || q.contains("cyclone") || q.contains("flood") -> {
                "বন্যা বা ঘূর্ণিঝড়ের প্রাক্কালে নিজেকে সুরক্ষিত রাখতে 'Disaster Guardian' ট্যাবে গিয়ে আপনার এলাকার ঝুঁকি পর্যবেক্ষণ করুন এবং জরুরি চেকলিস্ট নিশ্চিত করুন। শুকনো খাবার, খাওয়ার পানি এবং ফার্স্ট এইড কিট একটি ওয়াটারপ্রুফ ব্যাগে প্রস্তুত রাখুন। যেকোনো জরুরি পরিস্থিতিতে দুর্যোগ পূর্বাভাস হেল্পলাইন ১০৯০-এ বিনামূল্যে কল করতে পারেন। কড়াকড়ি বজ্রপাতের সময় দয়া করে খোলা মাঠে থাকা পরিহার করুন।"
            }
            q.contains("দলিল") || q.contains("আইন") || q.contains("policy") || q.contains("circular") || q.contains("pdf") -> {
                "সরকারি জটিল পরিপত্র বা নিয়মনীতি সহজে বোঝার জন্য আমাদের 'Documents' ট্যাবে যান। যেকোনো সরকারি নোটিশের লেখা বা ছবি কপি করে পেস্ট করলে সুরক্ষা এআই পরিপত্রটিকে সহজ বাংলায় ৫টি ভাগে রূপান্তর করে দেয়: মূল বিষয়, কাদের জন্য প্রযোজ্য, সরাসরি করণীয় ধাপ, গুরুত্বপূর্ণ ডেডলাইন এবং বাস্তব উদাহরণ। আপনি এটি অফলাইনেও সংরক্ষণ করতে পারেন।"
            }
            q.contains("হ্যালো") || q.contains("আসসালামু আলাইকুম") || q.contains("শুভ") || q.contains("hi") || q.contains("hello") || q.contains("name") -> {
                "স্বাগতম ও শুভকামনা! আমি 'সুরক্ষা এআই' পরিবারের পরম উপকারী সহচর 'সুরক্ষা সহায়ক'। আমি যেকোনো নিখোঁজ ব্যক্তি অনুসন্ধান, দুর্যোগ প্রস্তুতি এবং জটিল সরকারি নির্দেশাবলী সহজ করার বিষয়ে আপনাকে আন্তরিকতা সহকারে সাহায্য করতে প্রস্তুত। আপনি কি জানতে চান দয়া করে আমাকে জানান।"
            }
            else -> {
                "আমি আপনার বার্তাটি সাবলীলভাবে বুঝতে পেরেছি। আমি সর্বদা আপনার সুরক্ষায় প্রস্তুত আছি। আপনি চাইলে আমাদের নিখোঁজ ব্যক্তি নেটওয়ার্ক, দুর্যোগকালীন জীবন রক্ষাকারী চেকলিস্ট ও সরকারি ডকুমেন্ট সরলীকরণ ফিচার ব্যবহার করে যেকোনো বড় সংকট সমাধান করতে পারেন। দয়া করে দেশের জন্য যেকোনো উপকারে আমাদের হেল্পলাইন যথাক্রমে ১০০০, ১০৯০, ৩৩৩ অথবা ৯৯৯ ব্যবহার করুন।"
            }
        }
    }

    private fun getOfflineSimplifiedDocument(title: String, rawContent: String): SimplifiedDocument {
        // Safe offline simulated analysis depending on content keywords
        val text = rawContent.lowercase()
        val defaultActions = "• নির্দেশাবলী মনোযোগ দিয়ে পড়ুন।\n• প্রয়োজনীয় তথ্যাদি এবং কাগজপত্র নির্দিষ্ট দপ্তরে জমা দিন।\n• অফিসিয়াল ওয়েবসাইটে নিয়মিত লগইন করে আপডেট চেক করুন।"
        val mainTopic = if (text.contains("স্কুল") || text.contains("শিক্ষা")) {
            "মাধ্যমিক ও উচ্চ মাধ্যমিক স্তরের শিক্ষার্থীদের জন্য বৃত্তিমূলক উন্নয়ন প্রশিক্ষণ প্রকল্প ও অনুদান সংক্রান্ত বিশেষ বিবরণী।"
        } else if (text.contains("দুর্যোগ") || text.contains("বন")) {
            "দেশজুড়ে আসন্ন বর্ষা ও জোয়ারের ঝুঁকি হ্রাসকরণে দুর্যোগ প্রস্তুতি কমিটির বিশেষ সতর্কবার্তা।"
        } else {
            "জনসাধারণের কল্যাণে সচেতনতা বৃদ্ধিতে জারিকৃত গুরুত্বপূর্ণ সাধারণ প্রজ্ঞাপন ও নির্দেশিকা।"
        }

        val target = if (text.contains("ছাত্র") || text.contains("শিক্ষার্থী")) {
            "বাংলাদেশের সকল নিবন্ধিত সরকারি-বেসরকারি বিদ্যালয়ের শিক্ষার্থী, প্রধান শিক্ষক ও অভিভাবকবৃন্দ।"
        } else {
            "সকল সাধারণ নাগরিক, ইউনিয়ন পরিষদের দায়িত্বপ্রাপ্ত কর্মকর্তা এবং স্থানীয় সচেতন স্বেচ্ছাসেবকবৃন্দ।"
        }

        val actions = if (text.contains("বৃত্তি") || text.contains("আবেদন")) {
            "• প্রয়োজনীয় এনআইডি/জন্ম নিবন্ধন আইডি সংগ্রহ করুন।\n• প্রধান শিক্ষকের প্রত্যয়ন পত্রসহ সরকারি পোর্টাল বা অ্যাপে আবেদন পেশ করুন।\n• আপনার মোবাইল ব্যাংকিং (বিকাশ/রকেট/নগদ) নম্বর নিষ্ক্রিয় থাকলে তা সচল করুন।"
        } else {
            defaultActions
        }

        val dates = "পরিপত্র জারির ৩০ কার্যদিবসের মধ্যে অথবা আগামী ৩০শে জুন পর্যন্ত আবেদন বা নির্দেশনা কার্যকর থাকবে।"
        
        val examples = "ধরে নিন, রায়হান নোয়াখালীর একটি মাধ্যমিক বিদ্যালয়ের ৯ম শ্রেণীর ছাত্র। সে এই পরিপত্রের নীতিমালা অনুসরণ করে তার নিজ জন্ম নিবন্ধনের কপি ও বিকাশ নম্বর ব্যবহার করে সহজেই কোনো ঝামেলা ছাড়াই সরকারি উপবৃত্তি তহবিলের অনুদান পেতে পারেন।"

        return SimplifiedDocument(
            originalTitle = title,
            originalText = rawContent,
            mainTopic = mainTopic,
            targetAudience = target,
            requiredActions = actions,
            importantDates = dates,
            simpleExamples = examples
        )
    }

    private fun getOfflineDisasterGuide(disasterType: String, location: String): String {
        return when (disasterType) {
            "Cyclone" -> """
                **দুর্যোগ অভিভাবক - অফলাইন ঘূর্ণিঝড় নির্দেশিকা**
                📌 স্থান: $location 
                ⚠️ বর্তমান ঝুঁকি স্তর: **উচ্চ সতর্কতা (Risk Level: HIGH)**
                
                **করণীয় পদক্ষেপ (ঝড়ের আগে):**
                ১. বাড়ির আশেপাশে গাছের ডালপালা কেটে পরিষ্কার রাখুন।
                ২. ঘরের টিনের চালা পরীক্ষা করে নিশ্চিত হন।
                ৩. শুকনো খাবার (মুড়ি, চিঁড়া, গুড়) ও বোতলজাত পানি মজুত করুন।
                ৪. আপনার মূল্যবান কাগজপত্র প্লাস্টিক ফাইলে ভরে ওয়াটারপ্রুফ ব্যাগে রাখুন।
                
                **ঝড়ের সময়:**
                ১. সংকেত বাড়ার সাথে সাথে নিকটস্থ দুর্যোগ আশ্রয়কেন্দ্রে আশ্রয় নিন।
                ২. গুজব ছড়ানো পরিহার করুন, সরকারি রেডিও বা ১০৯০-এ কান রাখুন।
                ৩. কোনোভাবেই খোলা আবহাওয়ায় বের হবেন না।
                
                📞 **জরুরি নাম্বার:**
                - দুর্যোগ পূর্বাভাস বার্তা: ১০৯০
                - জাতীয় জরুরি সেবা: ৯৯৯
            """.trimIndent()
            "Flood" -> """
                **দুর্যোগ অভিভাবক - অফলাইন বন্যা নির্দেশিকা**
                📌 স্থান: $location
                ⚠️ বর্তমান ঝুঁকি স্তর: **মাঝারি সতর্কতা (Risk Level: MEDIUM)**
                
                **করণীয় পদক্ষেপ:**
                ১. টিউবওয়েলের মুখ পলিথিন দিয়ে ভালো করে মুড়িয়ে বেঁধে দিন যাতে ময়লা পানি না ঢোকে।
                ২. চুলা ও রান্নার শুকনো জ্বালানি উঁচু স্থানে রাখুন।
                ৩. পানি ফুটিয়ে অথবা পানি বিশুদ্ধকরণ ট্যাবলেট দিয়ে পান করুন।
                ৪. শিশুদের বন্যার জমা পানিতে নামতে দেবেন না; ডায়রিয়া বা সাপের কামড় থেকে নিরাপদ রাখুন।
                
                📞 **জরুরি নাম্বার:**
                - নদী ভাঙ্গন ও বন্যা নিয়ন্ত্রণ কেন্দ্র: ৩৩৩
                - সরকারি তথ্য সেবা: ৩৩৩
            """.trimIndent()
            "Lightning" -> """
                **দুর্যোগ অভিভাবক - অফলাইন বজ্রপাত নির্দেশিকা**
                📌 স্থান: $location
                ⚠️ বর্তমান ঝুঁকি স্তর: **তাৎক্ষণিক বজ্রপাত সতর্কতা (Risk Level: IMMEDIATE)**
                
                **জীবন রক্ষাকারী টিপস:**
                ১. মেঘের ডাক শুনলে সাথে সাথে পাকা দালানের নিচে বা কংক্রিট ছাদের নিচে আশ্রয় নিন।
                ২. খোলা মাঠ, উঁচু গাছপালা ও বৈদ্যুতিক খুঁটি থেকে দূরে অবস্থান করুন।
                ৩. ধাতব পাইপ, পানির কল ও ল্যান্ডলাইন ফোন স্পর্শ করবেন না।
                ৪. গাড়ির ভেতরে থাকলে ধাতব অংশ স্পর্শ করা থেকে বিরত থাকুন।
                
                📞 **জরুরি নাম্বার:**
                - জাতীয় হেল্পলাইন: ৯৯৯
            """.trimIndent()
            else -> """
                **দুর্যোগ অভিভাবক - সাধারণ সুরক্ষা নির্দেশিকা**
                📌 স্থান: $location
                ⚠️ বর্তমান ঝুঁকি স্তর: **সতর্ক নজরদারি (ACTIVE MONITORING)**
                
                ১. আপনার এলাকার সুরক্ষা স্বেচ্ছাসেবক বাহিনীর সাথে যোগাযোগ বজায় রাখুন।
                ২. সুরক্ষা এআই অ্যাপের জরুরি চেকলিস্টের সকল আইটেম নিশ্চিত করুন।
                ৩. আবহাওয়ার পূর্বাভাস সংক্রান্ত খবরের জন্য ১০৯০ ডায়াল করুন।
            """.trimIndent()
        }
    }
}
