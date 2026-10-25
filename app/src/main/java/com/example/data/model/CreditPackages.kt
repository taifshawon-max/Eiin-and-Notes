package com.example.data.model

import java.util.Locale

data class SchoolCreditPackage(
    val schoolRecords: Int,
    val priceUsd: Double,
    val sku: String,
    val isPopular: Boolean = false
) {
    val formattedPrice: String
        get() = String.format(Locale.US, "$%.2f", priceUsd)
}

data class NoteCreditPackage(
    val noteCount: Int,
    val priceUsd: Double,
    val sku: String,
    val isPopular: Boolean = false
) {
    val formattedPrice: String
        get() = String.format(Locale.US, "$%.2f", priceUsd)
}

data class QaCreditPackage(
    val questionCount: Int,
    val priceUsd: Double,
    val sku: String,
    val isPopular: Boolean = false
) {
    val formattedPrice: String
        get() = String.format(Locale.US, "$%.2f", priceUsd)
}

object PricingCatalog {
    /**
     * School Search Credit Packages: 100 to 2,000 schools in increments of 100
     * Formula: Price in USD = Number of 100-school units × $1.00 + $0.01
     */
    val schoolPackages: List<SchoolCreditPackage> = (1..20).map { unit ->
        val schools = unit * 100
        val price = (unit * 1.00) + 0.01
        SchoolCreditPackage(
            schoolRecords = schools,
            priceUsd = price,
            sku = "school_credits_$schools",
            isPopular = schools == 500 || schools == 1000
        )
    }

    /**
     * Class Notes Packages: 1 to 20 notes in increments of 1
     * Formula: Price in USD = Number of notes × $1.00 - $0.01
     * Starts from $0.99 (1 note), $1.99 (2 notes), $2.99 (3 notes) ... up to $19.99 (20 notes)
     */
    val notePackages: List<NoteCreditPackage> = (1..20).map { count ->
        val price = ((count * 100) - 1) / 100.0
        NoteCreditPackage(
            noteCount = count,
            priceUsd = price,
            sku = "note_credits_$count",
            isPopular = count == 5 || count == 10
        )
    }

    /**
     * Question & Answer (Q&A) Packages: 1 to 20 questions in increments of 1
     * Formula: Price in USD = Number of questions × $1.00 + $0.01
     * Example: 1 question = $1.01, up to 20 questions = $20.01
     */
    val qaPackages: List<QaCreditPackage> = (1..20).map { count ->
        val price = (count * 1.00) + 0.01
        QaCreditPackage(
            questionCount = count,
            priceUsd = price,
            sku = "qa_credits_$count",
            isPopular = count == 5 || count == 10
        )
    }
}
