package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.UnlockedNoteEntity
import com.example.data.model.UnlockedQaEntity
import com.example.data.model.UnlockedSchoolEntity
import com.example.data.model.UserEntitlementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EntitlementDao {
    @Query("SELECT * FROM user_entitlements WHERE userId = :userId LIMIT 1")
    fun observeUserEntitlements(userId: String = "default_user"): Flow<UserEntitlementEntity?>

    @Query("SELECT * FROM user_entitlements WHERE userId = :userId LIMIT 1")
    suspend fun getUserEntitlements(userId: String = "default_user"): UserEntitlementEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserEntitlements(entitlements: UserEntitlementEntity)

    @Query("SELECT * FROM unlocked_schools ORDER BY unlockedAt DESC")
    fun observeUnlockedSchools(): Flow<List<UnlockedSchoolEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_schools WHERE schoolId = :schoolId)")
    suspend fun isSchoolUnlocked(schoolId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun recordSchoolUnlocked(unlocked: UnlockedSchoolEntity)

    @Query("SELECT * FROM unlocked_notes ORDER BY unlockedAt DESC")
    fun observeUnlockedNotes(): Flow<List<UnlockedNoteEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_notes WHERE noteId = :noteId)")
    suspend fun isNoteUnlocked(noteId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun recordNoteUnlocked(unlocked: UnlockedNoteEntity)

    @Query("SELECT * FROM unlocked_qa ORDER BY unlockedAt DESC")
    fun observeUnlockedQa(): Flow<List<UnlockedQaEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM unlocked_qa WHERE qaId = :qaId)")
    suspend fun isQaUnlocked(qaId: String): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun recordQaUnlocked(unlocked: UnlockedQaEntity)

    @Query("DELETE FROM unlocked_schools")
    suspend fun clearUnlockedSchools()

    @Query("DELETE FROM unlocked_notes")
    suspend fun clearUnlockedNotes()

    @Query("DELETE FROM unlocked_qa")
    suspend fun clearUnlockedQa()
}
