package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "class_notes")
data class ClassNoteEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subject: String, // Physics, Chemistry, Higher Math, Biology, ICT, Bangla, English
    val courseCode: String,
    val academicLevel: String, // Class 9-10 (SSC), Class 11-12 (HSC), Degree/Admission
    val author: String,
    val fileFormat: String = "PDF",
    val description: String,
    val sampleExcerpt: String, // Free preview text & summary
    val fullContent: String,   // Full academic note text / chapter breakdown
    val totalPages: Int,
    val priceCredits: Int = 1,
    val publishedDate: String = "2026-02-10",
    val rating: Float = 4.8f,
    val downloadsCount: Int = 142
)
