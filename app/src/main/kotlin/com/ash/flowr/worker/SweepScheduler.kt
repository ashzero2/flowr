package com.ash.flowr.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

private const val PERIODIC_WORK_NAME = "flowr_sweep_periodic"
private const val IMMEDIATE_WORK_NAME = "flowr_sweep_immediate"

@Singleton
class SweepScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    fun schedulePeriodicSweep() {
        val request = PeriodicWorkRequestBuilder<SmsSweepWorker>(6, TimeUnit.HOURS)
            .setConstraints(Constraints.NONE)
            .build()
        workManager.enqueueUniquePeriodicWork(
            PERIODIC_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun scheduleImmediateSweep() {
        val request = OneTimeWorkRequestBuilder<SmsSweepWorker>().build()
        workManager.enqueueUniqueWork(
            IMMEDIATE_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
