package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_entitlements")
data class UserEntitlementEntity(
    @PrimaryKey val userId: String = "default_user",
    val schoolCreditsPurchased: Int = 100, // Initial trial pack
    val schoolCreditsUsed: Int = 0,
    val noteCreditsPurchased: Int = 2,     // Initial 2 free sample note credits
    val noteCreditsUsed: Int = 0,
    val qaCreditsPurchased: Int = 2,       // Initial 2 Q&A credits
    val qaCreditsUsed: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    val schoolCreditsRemaining: Int
        get() = (schoolCreditsPurchased - schoolCreditsUsed).coerceAtLeast(0)

    val noteCreditsRemaining: Int
        get() = (noteCreditsPurchased - noteCreditsUsed).coerceAtLeast(0)

    val qaCreditsRemaining: Int
        get() = (qaCreditsPurchased - qaCreditsUsed).coerceAtLeast(0)
}

@Entity(tableName = "unlocked_schools")
data class UnlockedSchoolEntity(
    @PrimaryKey val schoolId: String,
    val unlockedAt: Long = System.currentTimeMillis(),
    val transactionRef: String = "CREDIT_GRANT"
)

@Entity(tableName = "unlocked_notes")
data class UnlockedNoteEntity(
    @PrimaryKey val noteId: String,
    val unlockedAt: Long = System.currentTimeMillis(),
    val transactionRef: String = "NOTE_CREDIT_GRANT"
)

@Entity(tableName = "unlocked_qa")
data class UnlockedQaEntity(
    @PrimaryKey val qaId: String,
    val unlockedAt: Long = System.currentTimeMillis(),
    val transactionRef: String = "QA_CREDIT_GRANT"
)

@Entity(tableName = "purchase_transactions")
data class PurchaseTransactionEntity(
    @PrimaryKey val transactionId: String,
    val productType: String, // "SCHOOL_CREDITS", "NOTE_CREDITS", or "QA_CREDITS"
    val productSku: String,
    val quantity: Int,
    val priceUsd: Double,
    val purchaseToken: String,
    val paymentProvider: String = "Google Play Billing",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "COMPLETED" // "COMPLETED", "PENDING", "REFUNDED"
)
