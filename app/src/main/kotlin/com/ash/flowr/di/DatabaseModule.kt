package com.ash.flowr.di

import android.content.Context
import androidx.room.Room
import com.ash.flowr.data.local.FlowrDatabase
import com.ash.flowr.data.local.dao.BankAccountDao
import com.ash.flowr.data.local.dao.HoldingDao
import com.ash.flowr.data.local.dao.PendingReviewDao
import com.ash.flowr.data.local.dao.ProcessedSmsDao
import com.ash.flowr.data.local.dao.RecurringTemplateDao
import com.ash.flowr.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): FlowrDatabase =
        Room.databaseBuilder(context, FlowrDatabase::class.java, "flowr.db")
            .fallbackToDestructiveMigration(true)
            .build()

    @Provides fun provideTransactionDao(db: FlowrDatabase): TransactionDao = db.transactionDao()
    @Provides fun provideBankAccountDao(db: FlowrDatabase): BankAccountDao = db.bankAccountDao()
    @Provides fun provideRecurringTemplateDao(db: FlowrDatabase): RecurringTemplateDao = db.recurringTemplateDao()
    @Provides fun provideProcessedSmsDao(db: FlowrDatabase): ProcessedSmsDao = db.processedSmsDao()
    @Provides fun providePendingReviewDao(db: FlowrDatabase): PendingReviewDao = db.pendingReviewDao()
    @Provides fun provideHoldingDao(db: FlowrDatabase): HoldingDao = db.holdingDao()
}
