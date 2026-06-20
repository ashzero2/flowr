package com.ash.flowr.ui.screen.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ash.flowr.data.repository.BankAccountRepository
import com.ash.flowr.data.repository.TransactionRepository
import com.ash.flowr.domain.Category
import com.ash.flowr.ui.screen.dashboard.AccountBalance
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.YearMonth
import javax.inject.Inject

data class CategoryTotal(val category: Category, val total: Double)
data class SpendVsIncome(val spend: Double, val income: Double)

@HiltViewModel
class StatsViewModel @Inject constructor(
    private val transactionRepo: TransactionRepository,
    private val bankAccountRepo: BankAccountRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    fun nextMonth() { _selectedMonth.value = _selectedMonth.value.plusMonths(1) }
    fun previousMonth() { _selectedMonth.value = _selectedMonth.value.minusMonths(1) }

    @OptIn(ExperimentalCoroutinesApi::class)
    private val monthTransactions = _selectedMonth.flatMapLatest { month ->
        val start = month.atDay(1).toEpochDay() * 86_400_000L
        val end = month.plusMonths(1).atDay(1).toEpochDay() * 86_400_000L
        transactionRepo.observeByMonth(start, end)
    }

    val categoryBreakdown: StateFlow<List<CategoryTotal>> = monthTransactions
        .map { txns ->
            Category.entries
                .mapNotNull { cat ->
                    val total = txns
                        .filter { it.type == "EXPENSE" && it.category == cat.name }
                        .sumOf { it.amount }
                    if (total > 0.0) CategoryTotal(cat, total) else null
                }
                .sortedByDescending { it.total }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val spendVsIncome: StateFlow<SpendVsIncome> = monthTransactions
        .map { txns ->
            SpendVsIncome(
                spend = txns.filter { it.type == "EXPENSE" }.sumOf { it.amount },
                income = txns.filter { it.type == "INCOME" }.sumOf { it.amount }
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SpendVsIncome(0.0, 0.0))

    val accountBalances: StateFlow<List<AccountBalance>> = combine(
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
}
