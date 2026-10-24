package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qa_questions")
data class QuestionAnswerEntity(
    @PrimaryKey val id: String,
    val questionTitle: String,
    val questionDetails: String,
    val category: String, // "EIIN & Admission", "Physics", "Chemistry", "Higher Math", "Biology", "ICT"
    val academicLevel: String, // "Class 9-10 (SSC)", "Class 11-12 (HSC)", "College Admission"
    val askedBy: String,
    val answerPreview: String,
    val fullAnswer: String,
    val answeredBy: String,
    val isUserAsked: Boolean = false,
    val createdAt: String = "2026-03-20",
    val helpfulCount: Int = 24
)
