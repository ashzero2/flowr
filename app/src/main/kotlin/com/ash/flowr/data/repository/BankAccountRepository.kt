package com.ash.flowr.data.repository

import com.ash.flowr.data.local.entity.BankAccountEntity
import kotlinx.coroutines.flow.Flow

interface BankAccountRepository {
    fun observeAll(): Flow<List<BankAccountEntity>>
    suspend fun getById(id: Long): BankAccountEntity?
    suspend fun getByLast4(last4: String): BankAccountEntity?
    suspend fun getBySmsIdentifier(senderId: String): BankAccountEntity?
    suspend fun insert(account: BankAccountEntity): Long
    suspend fun insertAll(accounts: List<BankAccountEntity>)
    suspend fun update(account: BankAccountEntity)
    suspend fun delete(account: BankAccountEntity)
}
