package com.ash.flowr.ui.screen.stats

import androidx.lifecycle.ViewModel
import com.ash.flowr.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(
    val transactionRepository: TransactionRepository
) : ViewModel()
