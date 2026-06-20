package com.ash.flowr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ash.flowr.data.local.entity.ProcessedSmsEntity

@Dao
interface ProcessedSmsDao {

    @Query("SELECT smsId FROM processed_sms")
    suspend fun getAllIds(): List<Long>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entry: ProcessedSmsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entries: List<ProcessedSmsEntity>)
}
