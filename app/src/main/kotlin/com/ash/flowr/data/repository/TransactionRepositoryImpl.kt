package com.ash.flowr.data.repository

import com.ash.flowr.data.local.dao.TransactionDao
import com.ash.flowr.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TransactionRepositoryImpl @Inject constructor(
    private val dao: TransactionDao
) : TransactionRepository {

    override fun observeAll(): Flow<List<TransactionEntity>> = dao.observeAll()
    override fun observeRecent(): Flow<List<TransactionEntity>> = dao.observeRecent()
    override fun observeByMonth(from: Long, to: Long): Flow<List<TransactionEntity>> = dao.observeByMonth(from, to)
    override fun observeByAccount(accountId: Long): Flow<List<TransactionEntity>> = dao.observeByAccount(accountId)
    override suspend fun getById(id: Long): TransactionEntity? = dao.getById(id)
    override suspend fun insert(transaction: TransactionEntity): Long = dao.insert(transaction)
    override suspend fun update(transaction: TransactionEntity) = dao.update(transaction)
    override suspend fun deleteById(id: Long) = dao.deleteById(id)
}
