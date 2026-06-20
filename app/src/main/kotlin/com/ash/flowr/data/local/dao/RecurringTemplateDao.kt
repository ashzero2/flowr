package com.ash.flowr.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ash.flowr.data.local.entity.RecurringTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringTemplateDao {

    @Query("SELECT * FROM recurring_templates ORDER BY name ASC")
    fun observeAll(): Flow<List<RecurringTemplateEntity>>

    @Query("SELECT * FROM recurring_templates WHERE nextDue <= :now AND isActive = 1")
    suspend fun getDue(now: Long): List<RecurringTemplateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: RecurringTemplateEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<RecurringTemplateEntity>)

    @Update
    suspend fun update(template: RecurringTemplateEntity)

    @Delete
    suspend fun delete(template: RecurringTemplateEntity)
}
