package com.ash.flowr.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ash.flowr.data.local.entity.BankAccountEntity
import com.ash.flowr.data.local.entity.TransactionEntity
import com.ash.flowr.data.repository.BankAccountRepository
import com.ash.flowr.data.repository.SmsInboxRepository
import com.ash.flowr.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import javax.inject.Inject

data class AccountBalance(val account: BankAccountEntity, val balance: Double)
data class MonthSummary(val totalExpense: Double, val totalIncome: Double, val totalTransfer: Double)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    transactionRepo: TransactionRepository,
    bankAccountRepo: BankAccountRepository,
    smsInboxRepository: SmsInboxRepository
) : ViewModel() {

    private val currentMonth = YearMonth.now()
    private val monthStart = currentMonth.atDay(1).toEpochDay() * 86_400_000L
    private val monthEnd = currentMonth.plusMonths(1).atDay(1).toEpochDay() * 86_400_000L

    val accounts: StateFlow<List<AccountBalance>> = combine(
        bankAccountRepo.observeAll(),
        transactionRepo.observeAll()
    ) { accounts, txns ->
        accounts.map { account ->
            val balance = account.startingBalance + txns.sumOf { t ->
                when {
                    t.type == "INCOME" && t.bankAccountId == account.id -> t.amount
                    t.type == "EXPENSE" && t.bankAccountId == account.id -> -t.amount
                    t.type == "TRANSFER" && t.bankAccountId == account.id -> -t.amount
                    t.type == "TRANSFER" && t.toBankAccountId == account.id -> t.amount
                    else -> 0.0
                }
            }
            AccountBalance(account, balance)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val recentTransactions: StateFlow<List<TransactionEntity>> = transactionRepo.observeRecent()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val monthSummary: StateFlow<MonthSummary> = transactionRepo.observeByMonth(monthStart, monthEnd)
        .map { txns ->
            MonthSummary(
                totalExpense = txns.filter { it.type == "EXPENSE" }.sumOf { it.amount },
                totalIncome = txns.filter { it.type == "INCOME" }.sumOf { it.amount },
                totalTransfer = txns.filter { it.type == "TRANSFER" }.sumOf { it.amount }
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MonthSummary(0.0, 0.0, 0.0))

    val pendingReviewCount: StateFlow<Int> = smsInboxRepository.observePendingCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)
}
