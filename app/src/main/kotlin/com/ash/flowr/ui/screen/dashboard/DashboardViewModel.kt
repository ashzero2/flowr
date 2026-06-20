package com.ash.flowr.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import com.ash.flowr.data.repository.BankAccountRepository
import com.ash.flowr.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    val transactionRepository: TransactionRepository,
    val bankAccountRepository: BankAccountRepository
) : ViewModel()
