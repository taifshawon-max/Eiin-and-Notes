package com.example.data.repository

import com.example.data.dao.ClassNoteDao
import com.example.data.dao.EntitlementDao
import com.example.data.db.InitialDataSeeder
import com.example.data.model.ClassNoteEntity
import com.example.data.model.UnlockedNoteEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class NotesRepository(
    private val classNoteDao: ClassNoteDao,
    private val entitlementDao: EntitlementDao
) {
    val allNotes: Flow<List<ClassNoteEntity>> = classNoteDao.getAllNotes()

    fun filterNotes(query: String, subject: String?, level: String?): Flow<List<ClassNoteEntity>> {
        val cleanSubject = if (subject == "All") null else subject
        val cleanLevel = if (level == "All") null else level
        return classNoteDao.filterNotes(query.trim(), cleanSubject, cleanLevel)
    }

    suspend fun getNoteById(id: String): ClassNoteEntity? = classNoteDao.getNoteById(id)

    suspend fun isNoteUnlocked(noteId: String): Boolean {
        return entitlementDao.isNoteUnlocked(noteId)
    }

    /**
     * Unlocks a class note using 1 note credit.
     * Guaranteed idempotent: If note is already unlocked, NO credit is deducted.
     */
    suspend fun unlockNote(noteId: String): Result<Boolean> {
        val alreadyUnlocked = entitlementDao.isNoteUnlocked(noteId)
        if (alreadyUnlocked) {
            return Result.success(true)
        }

        val entitlement = entitlementDao.getUserEntitlements()
            ?: InitialDataSeeder.getDefaultEntitlements()

        if (entitlement.noteCreditsRemaining <= 0) {
            return Result.failure(IllegalStateException("No note credits remaining. Please purchase a note package."))
        }

        // Deduct 1 note credit
        val updated = entitlement.copy(
            noteCreditsUsed = entitlement.noteCreditsUsed + 1,
            lastUpdated = System.currentTimeMillis()
        )
        entitlementDao.saveUserEntitlements(updated)

        // Mark note unlocked
        entitlementDao.recordNoteUnlocked(
            UnlockedNoteEntity(
                noteId = noteId,
                unlockedAt = System.currentTimeMillis(),
                transactionRef = "NOTE_UNLOCK_${UUID.randomUUID().toString().take(8)}"
            )
        )

        return Result.success(true)
    }

    suspend fun insertOrUpdateNote(note: ClassNoteEntity) {
        classNoteDao.insertNote(note)
    }

    suspend fun deleteNote(id: String) {
        classNoteDao.deleteNote(id)
    }

    suspend fun ensureDataSeeded() {
        if (classNoteDao.getNotesCount() == 0) {
            classNoteDao.insertNotes(InitialDataSeeder.sampleNotes)
        }
    }
}
