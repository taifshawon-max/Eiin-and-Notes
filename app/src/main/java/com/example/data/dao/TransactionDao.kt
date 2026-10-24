package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PurchaseTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM purchase_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<PurchaseTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: PurchaseTransactionEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM purchase_transactions WHERE transactionId = :transactionId)")
    suspend fun existsByTransactionId(transactionId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM purchase_transactions WHERE purchaseToken = :token)")
    suspend fun existsByPurchaseToken(token: String): Boolean

    @Query("UPDATE purchase_transactions SET status = :newStatus WHERE transactionId = :id")
    suspend fun updateStatus(id: String, newStatus: String)
}
