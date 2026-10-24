package com.example.data.repository

import com.example.data.dao.EntitlementDao
import com.example.data.dao.SchoolDao
import com.example.data.db.InitialDataSeeder
import com.example.data.model.SchoolEntity
import com.example.data.model.UnlockedSchoolEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class SchoolRepository(
    private val schoolDao: SchoolDao,
    private val entitlementDao: EntitlementDao
) {
    val allSchools: Flow<List<SchoolEntity>> = schoolDao.getAllSchools()
    val allDivisions: Flow<List<String>> = schoolDao.getAllDivisions()

    fun searchSchools(query: String): Flow<List<SchoolEntity>> {
        return if (query.isBlank()) {
            schoolDao.getAllSchools()
        } else {
            schoolDao.searchSchools(query.trim())
        }
    }

    suspend fun getSchoolById(id: String): SchoolEntity? = schoolDao.getSchoolById(id)

    suspend fun getSchoolByEiin(eiin: String): SchoolEntity? = schoolDao.getSchoolByEiin(eiin)

    suspend fun isSchoolUnlocked(schoolId: String): Boolean {
        return entitlementDao.isSchoolUnlocked(schoolId)
    }

    /**
     * Unlocks a school using 1 search credit.
     * Guaranteed idempotent: If the school is already unlocked, NO credit is deducted.
     */
    suspend fun unlockSchool(schoolId: String): Result<Boolean> {
        val alreadyUnlocked = entitlementDao.isSchoolUnlocked(schoolId)
        if (alreadyUnlocked) {
            return Result.success(true)
        }

        val entitlement = entitlementDao.getUserEntitlements()
            ?: InitialDataSeeder.getDefaultEntitlements()

        if (entitlement.schoolCreditsRemaining <= 0) {
            return Result.failure(IllegalStateException("No search credits remaining. Please purchase a credit package."))
        }

        // Deduct credit
        val updated = entitlement.copy(
            schoolCreditsUsed = entitlement.schoolCreditsUsed + 1,
            lastUpdated = System.currentTimeMillis()
        )
        entitlementDao.saveUserEntitlements(updated)

        // Mark school as unlocked
        entitlementDao.recordSchoolUnlocked(
            UnlockedSchoolEntity(
                schoolId = schoolId,
                unlockedAt = System.currentTimeMillis(),
                transactionRef = "CREDIT_DEDUCTION_${UUID.randomUUID().toString().take(8)}"
            )
        )

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
