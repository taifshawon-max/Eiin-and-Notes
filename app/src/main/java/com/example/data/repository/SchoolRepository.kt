package com.example.data.repository

import com.example.data.dao.EntitlementDao
import com.example.data.dao.SchoolDao
import com.example.data.db.InitialDataSeeder
import com.example.data.model.SchoolEntity
import com.example.data.model.UnlockedSchoolEntity
import com.example.data.remote.GoogleSchoolSearchService
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class SchoolRepository(
    private val schoolDao: SchoolDao,
    private val entitlementDao: EntitlementDao,
    private val googleSearchService: GoogleSchoolSearchService = GoogleSchoolSearchService()
) {
    val allSchools: Flow<List<SchoolEntity>> = schoolDao.getAllSchools()
    val allDivisions: Flow<List<String>> = schoolDao.getAllDivisions()

    fun searchSchoolsLocalCache(query: String): Flow<List<SchoolEntity>> {
        return if (query.isBlank()) {
            schoolDao.getAllSchools()
        } else {
            schoolDao.searchSchools(query.trim())
        }
    }

    /**
     * Searches Google live for any Bangladeshi school EIIN and caches discovered records
     * so unlocked schools persist in the user's library.
     */
    suspend fun searchGoogleLive(query: String): List<SchoolEntity> {
        val trimmed = query.trim()
        if (trimmed.isBlank()) return emptyList()
        val liveResults = googleSearchService.searchSchoolEiinOnGoogle(trimmed)
        if (liveResults.isNotEmpty()) {
            schoolDao.insertSchools(liveResults)
        }
        return liveResults
    }

    /**
     * Fetches live Google autocomplete suggestions as the user types in the search bar.
     */
    suspend fun fetchGoogleSuggestions(query: String): List<String> {
        return googleSearchService.fetchGoogleSuggestions(query)
    }

    /**
     * Parses live Google Search DOM results extracted from the Android WebView and caches them.
     */
    suspend fun parseAndCacheWebViewResults(query: String, jsonArrayStr: String): List<SchoolEntity> {
        val parsed = googleSearchService.parseWebViewDomResults(query, jsonArrayStr)
        if (parsed.isNotEmpty()) {
            schoolDao.insertSchools(parsed)
        }
        return parsed
    }

    suspend fun getSchoolById(id: String): SchoolEntity? = schoolDao.getSchoolById(id)

    suspend fun getSchoolByEiin(eiin: String): SchoolEntity? = schoolDao.getSchoolByEiin(eiin)

    suspend fun isSchoolUnlocked(schoolId: String, eiin: String = ""): Boolean {
        if (entitlementDao.isSchoolUnlocked(schoolId)) return true
        if (eiin.isNotBlank() && entitlementDao.isSchoolUnlocked("eiin_$eiin")) return true
        return false
    }

    /**
     * Unlocks a school using 1 search credit from the user's active credit balance.
     * Guaranteed idempotent: If the school (by ID or 6-digit EIIN) is already unlocked,
     * NO credit is deducted. Users can use their credits continuously until their limit is crossed.
     */
    suspend fun unlockSchool(school: SchoolEntity): Result<Boolean> {
        // Ensure Google-discovered school is persisted in local DB
        schoolDao.insertSchool(school)

        val eiinKey = "eiin_${school.eiin.trim()}"
        val alreadyUnlockedById = entitlementDao.isSchoolUnlocked(school.id)
        val alreadyUnlockedByEiin = school.eiin.isNotBlank() && entitlementDao.isSchoolUnlocked(eiinKey)

        if (alreadyUnlockedById || alreadyUnlockedByEiin) {
            // Ensure both keys are recorded so UI always sees it as unlocked
            val now = System.currentTimeMillis()
            if (!alreadyUnlockedById) {
                entitlementDao.recordSchoolUnlocked(UnlockedSchoolEntity(schoolId = school.id, unlockedAt = now))
            }
            if (school.eiin.isNotBlank() && !alreadyUnlockedByEiin) {
                entitlementDao.recordSchoolUnlocked(UnlockedSchoolEntity(schoolId = eiinKey, unlockedAt = now))
            }
            return Result.success(true)
        }

        val entitlement = entitlementDao.getUserEntitlements() ?: run {
            val defaultEnt = InitialDataSeeder.getDefaultEntitlements()
            entitlementDao.saveUserEntitlements(defaultEnt)
            defaultEnt
        }

        if (entitlement.schoolCreditsRemaining <= 0) {
            return Result.failure(
                IllegalStateException("Credit limit reached (0 of ${entitlement.schoolCreditsPurchased} credits left). Please purchase a credit package.")
            )
        }

        // Deduct 1 credit from remaining limit
        val updated = entitlement.copy(
            schoolCreditsUsed = entitlement.schoolCreditsUsed + 1,
            lastUpdated = System.currentTimeMillis()
        )
        entitlementDao.saveUserEntitlements(updated)

        // Mark school as unlocked by both ID and EIIN so it is never charged twice
        val now = System.currentTimeMillis()
        val ref = "CREDIT_USE_${UUID.randomUUID().toString().take(8)}"
        entitlementDao.recordSchoolUnlocked(
            UnlockedSchoolEntity(
                schoolId = school.id,
                unlockedAt = now,
                transactionRef = ref
            )
        )
        if (school.eiin.isNotBlank()) {
            entitlementDao.recordSchoolUnlocked(
                UnlockedSchoolEntity(
                    schoolId = eiinKey,
                    unlockedAt = now,
                    transactionRef = ref
                )
            )
        }

        return Result.success(true)
    }

    suspend fun insertOrUpdateSchool(school: SchoolEntity) {
        schoolDao.insertSchool(school)
    }

    suspend fun deleteSchool(id: String) {
        schoolDao.deleteSchool(id)
    }

    suspend fun ensureDataSeeded() {
        if (schoolDao.getSchoolCount() == 0) {
            schoolDao.insertSchools(InitialDataSeeder.sampleSchools)
        }
    }
}
