package com.ash.flowr.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.ash.flowr.data.local.entity.PendingReviewEntity
import com.ash.flowr.data.repository.SmsInboxRepository
import com.ash.flowr.data.datastore.UserPreferencesRepository
import com.ash.flowr.data.sms.SmsTransactionSource
import com.ash.flowr.notification.FlowrNotificationManager
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class SmsSweepWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val smsTransactionSource: SmsTransactionSource,
    private val smsInboxRepository: SmsInboxRepository,
    private val prefs: UserPreferencesRepository,
    private val notificationManager: FlowrNotificationManager
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val since = prefs.lastSuccessfulSweep.first()
        val now = System.currentTimeMillis()

        val parsed = try {
            smsTransactionSource.fetch(since, now)
        } catch (e: Exception) {
            return Result.retry()
        }

        if (parsed.isNotEmpty()) {
            val pending = parsed.map { txn ->
                PendingReviewEntity(
                    smsId = txn.smsId,
                    amount = txn.amount,
                    direction = txn.direction.name,
                    bankAccountId = txn.bankAccountId,
                    merchantName = txn.merchantName,
                    vpa = txn.vpa,
                    refNo = txn.refNo,
                    date = txn.date,
                    rawBody = txn.rawBody,
                    senderAddress = txn.senderAddress
                )
            }
            smsInboxRepository.insertPending(pending)
            notificationManager.sendReviewNotification(parsed.size)
        }

        prefs.setLastSuccessfulSweep(now)
        return Result.success()
    }
}
