package com.ash.flowr.data.repository

import com.ash.flowr.data.local.dao.BankAccountDao
import com.ash.flowr.data.local.entity.BankAccountEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BankAccountRepositoryImpl @Inject constructor(
    private val dao: BankAccountDao
) : BankAccountRepository {

    override fun observeAll(): Flow<List<BankAccountEntity>> = dao.observeAll()
    override suspend fun getById(id: Long): BankAccountEntity? = dao.getById(id)
    override suspend fun getByLast4(last4: String): BankAccountEntity? = dao.getByLast4(last4)
    override suspend fun getBySmsIdentifier(senderId: String): BankAccountEntity? = dao.getBySmsIdentifier(senderId)
    override suspend fun insert(account: BankAccountEntity): Long = dao.insert(account)
    override suspend fun insertAll(accounts: List<BankAccountEntity>) = dao.insertAll(accounts)
    override suspend fun update(account: BankAccountEntity) = dao.update(account)
    override suspend fun delete(account: BankAccountEntity) = dao.delete(account)
}
