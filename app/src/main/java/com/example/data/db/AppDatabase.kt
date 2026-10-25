package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.ClassNoteDao
import com.example.data.dao.EntitlementDao
import com.example.data.dao.QuestionAnswerDao
import com.example.data.dao.SchoolDao
import com.example.data.dao.TransactionDao
import com.example.data.model.ClassNoteEntity
import com.example.data.model.PurchaseTransactionEntity
import com.example.data.model.QuestionAnswerEntity
import com.example.data.model.SchoolEntity
import com.example.data.model.UnlockedNoteEntity
import com.example.data.model.UnlockedQaEntity
import com.example.data.model.UnlockedSchoolEntity
import com.example.data.model.UserEntitlementEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        SchoolEntity::class,
        ClassNoteEntity::class,
        QuestionAnswerEntity::class,
        UserEntitlementEntity::class,
        UnlockedSchoolEntity::class,
        UnlockedNoteEntity::class,
        UnlockedQaEntity::class,
        PurchaseTransactionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun schoolDao(): SchoolDao
    abstract fun classNoteDao(): ClassNoteDao
    abstract fun questionAnswerDao(): QuestionAnswerDao
    abstract fun entitlementDao(): EntitlementDao
    abstract fun transactionDao(): TransactionDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "school_eiin_notes.db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getInstance(context)
                            database.schoolDao().insertSchools(InitialDataSeeder.sampleSchools)
                            database.classNoteDao().insertNotes(InitialDataSeeder.sampleNotes)
                            database.questionAnswerDao().insertQuestions(InitialDataSeeder.sampleQuestions)
                            database.entitlementDao().saveUserEntitlements(InitialDataSeeder.getDefaultEntitlements())
                        }
                    }
                })
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
