package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ClassNoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ClassNoteDao {
    @Query("SELECT * FROM class_notes ORDER BY publishedDate DESC")
    fun getAllNotes(): Flow<List<ClassNoteEntity>>

    @Query("SELECT * FROM class_notes WHERE id = :id LIMIT 1")
    suspend fun getNoteById(id: String): ClassNoteEntity?

    @Query("""
        SELECT * FROM class_notes 
        WHERE (:subject IS NULL OR subject = :subject)
          AND (:level IS NULL OR academicLevel = :level)
          AND (title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' OR author LIKE '%' || :query || '%')
        ORDER BY publishedDate DESC
    """)
    fun filterNotes(query: String, subject: String?, level: String?): Flow<List<ClassNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotes(notes: List<ClassNoteEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: ClassNoteEntity)

    @Query("DELETE FROM class_notes WHERE id = :id")
    suspend fun deleteNote(id: String)

    @Query("SELECT COUNT(*) FROM class_notes")
    suspend fun getNotesCount(): Int
}
