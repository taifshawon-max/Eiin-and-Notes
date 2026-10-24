package com.example.data.repository

import com.example.data.dao.EntitlementDao
import com.example.data.db.InitialDataSeeder
import com.example.data.model.UnlockedNoteEntity
import com.example.data.model.UnlockedQaEntity
import com.example.data.model.UnlockedSchoolEntity
import com.example.data.model.UserEntitlementEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class EntitlementRepository(
    private val entitlementDao: EntitlementDao
) {
    val entitlements: Flow<UserEntitlementEntity> =
        entitlementDao.observeUserEntitlements().map { it ?: InitialDataSeeder.getDefaultEntitlements() }

    val unlockedSchools: Flow<List<UnlockedSchoolEntity>> = entitlementDao.observeUnlockedSchools()
    val unlockedNotes: Flow<List<UnlockedNoteEntity>> = entitlementDao.observeUnlockedNotes()
    val unlockedQa: Flow<List<UnlockedQaEntity>> = entitlementDao.observeUnlockedQa()

    suspend fun getEntitlements(): UserEntitlementEntity {
        return entitlementDao.getUserEntitlements() ?: run {
            val defaultEnt = InitialDataSeeder.getDefaultEntitlements()
            entitlementDao.saveUserEntitlements(defaultEnt)
            defaultEnt
        }
    }

    suspend fun addCredits(schoolCredits: Int = 0, noteCredits: Int = 0, qaCredits: Int = 0) {
        val current = getEntitlements()
        val updated = current.copy(
            schoolCreditsPurchased = current.schoolCreditsPurchased + schoolCredits,
            noteCreditsPurchased = current.noteCreditsPurchased + noteCredits,
            qaCreditsPurchased = current.qaCreditsPurchased + qaCredits,
            lastUpdated = System.currentTimeMillis()
        )
        entitlementDao.saveUserEntitlements(updated)
    }

    suspend fun resetAllForTesting() {
        val resetEnt = InitialDataSeeder.getDefaultEntitlements()
        entitlementDao.saveUserEntitlements(resetEnt)
        entitlementDao.clearUnlockedSchools()
        entitlementDao.clearUnlockedNotes()
        entitlementDao.clearUnlockedQa()
    }
}
