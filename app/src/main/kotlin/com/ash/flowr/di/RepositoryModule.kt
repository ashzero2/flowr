package com.ash.flowr.di

import com.ash.flowr.data.repository.BankAccountRepository
import com.ash.flowr.data.repository.BankAccountRepositoryImpl
import com.ash.flowr.data.repository.SmsInboxRepository
import com.ash.flowr.data.repository.SmsInboxRepositoryImpl
import com.ash.flowr.data.repository.TransactionRepository
import com.ash.flowr.data.repository.TransactionRepositoryImpl
import com.ash.flowr.data.sms.SmsTransactionSource
import com.ash.flowr.domain.TransactionSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindTransactionRepository(impl: TransactionRepositoryImpl): TransactionRepository

    @Binds @Singleton
    abstract fun bindBankAccountRepository(impl: BankAccountRepositoryImpl): BankAccountRepository

    @Binds @Singleton
    abstract fun bindSmsInboxRepository(impl: SmsInboxRepositoryImpl): SmsInboxRepository

    @Binds @Singleton
    abstract fun bindTransactionSource(impl: SmsTransactionSource): TransactionSource
}
