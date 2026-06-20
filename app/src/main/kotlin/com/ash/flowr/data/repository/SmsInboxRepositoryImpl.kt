package com.ash.flowr.data.repository

import com.ash.flowr.data.local.dao.PendingReviewDao
import com.ash.flowr.data.local.dao.ProcessedSmsDao
import com.ash.flowr.data.local.entity.PendingReviewEntity
import com.ash.flowr.data.local.entity.ProcessedSmsEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsInboxRepositoryImpl @Inject constructor(
    private val pendingReviewDao: PendingReviewDao,
    private val processedSmsDao: ProcessedSmsDao
) : SmsInboxRepository {

    override fun observePending(): Flow<List<PendingReviewEntity>> =
        pendingReviewDao.observeAll()

    override fun observePendingCount(): Flow<Int> =
        pendingReviewDao.observeCount()

    override suspend fun getProcessedSmsIds(): Set<Long> =
        processedSmsDao.getAllIds().toSet()

    override suspend fun insertPending(items: List<PendingReviewEntity>) =
        pendingReviewDao.insertAll(items)

    override suspend fun confirmAndDelete(smsId: Long) {
        processedSmsDao.insert(
            ProcessedSmsEntity(
                smsId = smsId,
                processedAt = System.currentTimeMillis(),
                wasImported = true
            )
        )
        pendingReviewDao.deleteById(smsId)
    }

    override suspend fun skipAndDelete(smsId: Long) {
        processedSmsDao.insert(
            ProcessedSmsEntity(
                smsId = smsId,
                processedAt = System.currentTimeMillis(),
                wasImported = false
            )
        )
        pendingReviewDao.deleteById(smsId)
    }
}
