package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MissingPersonDao {
    @Query("SELECT * FROM missing_person_reports ORDER BY id DESC")
    fun getAllReports(): Flow<List<MissingPersonReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: MissingPersonReport): Long

    @Update
    suspend fun updateReport(report: MissingPersonReport)

    @Delete
    suspend fun deleteReport(report: MissingPersonReport)
}

@Dao
interface DisasterChecklistDao {
    @Query("SELECT * FROM disaster_checklists ORDER BY category, id ASC")
    fun getAllChecklistItems(): Flow<List<DisasterChecklist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItem(item: DisasterChecklist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChecklistItems(items: List<DisasterChecklist>)

    @Update
    suspend fun updateChecklistItem(item: DisasterChecklist)

    @Delete
    suspend fun deleteChecklistItem(item: DisasterChecklist)
}

@Dao
interface SimplifiedDocumentDao {
    @Query("SELECT * FROM simplified_documents ORDER BY dateSimplified DESC")
    fun getAllSimplifiedDocuments(): Flow<List<SimplifiedDocument>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSimplifiedDocument(doc: SimplifiedDocument): Long

    @Delete
    suspend fun deleteSimplifiedDocument(doc: SimplifiedDocument)
}

@Dao
interface AnalyticsDao {
    @Query("SELECT * FROM analytics_records WHERE id = 1 LIMIT 1")
    fun getAnalytics(): Flow<AnalyticsRecord?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAnalytics(analytics: AnalyticsRecord)
}
