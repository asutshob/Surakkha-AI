package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missing_person_reports")
data class MissingPersonReport(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val age: Int,
    val lastSeenLocation: String,
    val description: String,
    val contactPhone: String,
    val photoUrl: String, // String representation of URI or web URL or local placeholder index
    val reporterName: String,
    val dateReported: String,
    val status: String, // PENDING, ACTIVE, FOUND
    val isRegionalAlertSent: Boolean = false,
    val matchScore: Double = 0.0 // Face recognition simulate/actual score
)

@Entity(tableName = "disaster_checklists")
data class DisasterChecklist(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val itemText: String,
    val isChecked: Boolean,
    val category: String // Flood, Cyclone, Lightning, RiverErosion, General
)

@Entity(tableName = "simplified_documents")
data class SimplifiedDocument(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalTitle: String,
    val originalText: String,
    val mainTopic: String,
    val targetAudience: String,
    val requiredActions: String,
    val importantDates: String,
    val simpleExamples: String,
    val dateSimplified: Long = System.currentTimeMillis()
)

@Entity(tableName = "analytics_records")
data class AnalyticsRecord(
    @PrimaryKey val id: Int = 1,
    val solvedMissingPersons: Int = 12,
    val activeMissingSearches: Int = 4,
    val simplifiedDocumentsCount: Int = 3,
    val disasterChecklistsCompleted: Int = 2,
    val disasterReadyStatus: String = "Medium Risk - Active Monitoring",
    val chatInteractionsCount: Int = 18,
    val communityVolunteersRegistered: Int = 1420
)
