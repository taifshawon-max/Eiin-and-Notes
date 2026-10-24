package com.example.data.model

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "schools",
    indices = [
        Index(value = ["eiin"], unique = true),
        Index(value = ["name"]),
        Index(value = ["district"])
    ]
)
data class SchoolEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nameBn: String,
    val eiin: String,
    val division: String,
    val district: String,
    val upazila: String,
    val institutionType: String, // High School, College, School & College, Cadet College, Madrasah
    val board: String, // Dhaka, Chattogram, Rajshahi, Cumilla, Barishal, Sylhet, Dinajpur, Mymensingh, Madrasah
    val address: String,
    val establishedYear: Int? = null,
    val isVerified: Boolean = true,
    val dataSource: String = "BANBEIS / Ministry of Education, Bangladesh",
    val lastVerifiedDate: String = "2026-03-15"
)
