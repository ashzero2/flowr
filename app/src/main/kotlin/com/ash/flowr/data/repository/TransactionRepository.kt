package com.ash.flowr.data.repository

import com.ash.flowr.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

interface TransactionRepository {
    fun observeAll(): Flow<List<TransactionEntity>>
    fun observeRecent(): Flow<List<TransactionEntity>>
    fun observeByMonth(from: Long, to: Long): Flow<List<TransactionEntity>>
    fun observeByAccount(accountId: Long): Flow<List<TransactionEntity>>
    suspend fun getById(id: Long): TransactionEntity?
    suspend fun insert(transaction: TransactionEntity): Long
    suspend fun update(transaction: TransactionEntity)
    suspend fun deleteById(id: Long)
}
