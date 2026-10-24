package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.QuestionAnswerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface QuestionAnswerDao {
    @Query("SELECT * FROM qa_questions ORDER BY isUserAsked DESC, createdAt DESC")
    fun getAllQuestions(): Flow<List<QuestionAnswerEntity>>

    @Query("SELECT * FROM qa_questions WHERE id = :id LIMIT 1")
    suspend fun getQuestionById(id: String): QuestionAnswerEntity?

    @Query("""
        SELECT * FROM qa_questions
        WHERE (:category IS NULL OR category = :category)
          AND (:level IS NULL OR academicLevel = :level)
          AND (questionTitle LIKE '%' || :query || '%' OR questionDetails LIKE '%' || :query || '%' OR answeredBy LIKE '%' || :query || '%')
        ORDER BY isUserAsked DESC, createdAt DESC
    """)
    fun filterQuestions(query: String, category: String?, level: String?): Flow<List<QuestionAnswerEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<QuestionAnswerEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: QuestionAnswerEntity)

    @Query("DELETE FROM qa_questions WHERE id = :id")
    suspend fun deleteQuestion(id: String)

    @Query("SELECT COUNT(*) FROM qa_questions")
    suspend fun getQuestionsCount(): Int
}
