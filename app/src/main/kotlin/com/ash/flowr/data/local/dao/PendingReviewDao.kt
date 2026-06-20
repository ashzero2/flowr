package com.ash.flowr.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ash.flowr.data.local.entity.PendingReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingReviewDao {

    @Query("SELECT * FROM pending_review ORDER BY date DESC")
    fun observeAll(): Flow<List<PendingReviewEntity>>

    @Query("SELECT COUNT(*) FROM pending_review")
    fun observeCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<PendingReviewEntity>)

    @Query("DELETE FROM pending_review WHERE smsId = :smsId")
    suspend fun deleteById(smsId: Long)

    @Query("DELETE FROM pending_review")
    suspend fun deleteAll()
}
