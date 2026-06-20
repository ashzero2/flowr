package com.ash.flowr.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ash.flowr.data.local.entity.BankAccountEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BankAccountDao {

    @Query("SELECT * FROM bank_accounts ORDER BY sortOrder ASC")
    fun observeAll(): Flow<List<BankAccountEntity>>

    @Query("SELECT * FROM bank_accounts WHERE id = :id")
    suspend fun getById(id: Long): BankAccountEntity?

    @Query("SELECT * FROM bank_accounts WHERE accountLast4 = :last4 LIMIT 1")
    suspend fun getByLast4(last4: String): BankAccountEntity?

    @Query("SELECT * FROM bank_accounts WHERE smsIdentifier = :senderId LIMIT 1")
    suspend fun getBySmsIdentifier(senderId: String): BankAccountEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(account: BankAccountEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(accounts: List<BankAccountEntity>)

    @Update
    suspend fun update(account: BankAccountEntity)

    @Delete
    suspend fun delete(account: BankAccountEntity)
}
