package com.ash.flowr.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ash.flowr.data.local.dao.BankAccountDao
import com.ash.flowr.data.local.dao.HoldingDao
import com.ash.flowr.data.local.dao.PendingReviewDao
import com.ash.flowr.data.local.dao.ProcessedSmsDao
import com.ash.flowr.data.local.dao.RecurringTemplateDao
import com.ash.flowr.data.local.dao.TransactionDao
import com.ash.flowr.data.local.entity.BankAccountEntity
import com.ash.flowr.data.local.entity.HoldingEntity
import com.ash.flowr.data.local.entity.PendingReviewEntity
import com.ash.flowr.data.local.entity.ProcessedSmsEntity
import com.ash.flowr.data.local.entity.RecurringTemplateEntity
import com.ash.flowr.data.local.entity.TransactionEntity

@Database(
    entities = [
        TransactionEntity::class,
        BankAccountEntity::class,
        RecurringTemplateEntity::class,
        ProcessedSmsEntity::class,
        PendingReviewEntity::class,
        HoldingEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class FlowrDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun bankAccountDao(): BankAccountDao
    abstract fun recurringTemplateDao(): RecurringTemplateDao
    abstract fun processedSmsDao(): ProcessedSmsDao
    abstract fun pendingReviewDao(): PendingReviewDao
    abstract fun holdingDao(): HoldingDao
}
