package com.ash.flowr.data.repository

import com.ash.flowr.data.local.entity.PendingReviewEntity
import kotlinx.coroutines.flow.Flow

interface SmsInboxRepository {
    fun observePending(): Flow<List<PendingReviewEntity>>
    fun observePendingCount(): Flow<Int>
    suspend fun getProcessedSmsIds(): Set<Long>
    suspend fun insertPending(items: List<PendingReviewEntity>)
    suspend fun confirmAndDelete(smsId: Long)
    suspend fun skipAndDelete(smsId: Long)
}
