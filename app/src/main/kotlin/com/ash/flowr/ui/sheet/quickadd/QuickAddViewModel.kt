package com.ash.flowr.ui.sheet.quickadd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ash.flowr.data.datastore.UserPreferencesRepository
import com.ash.flowr.data.local.entity.BankAccountEntity
import com.ash.flowr.data.local.entity.TransactionEntity
import com.ash.flowr.data.repository.BankAccountRepository
import com.ash.flowr.domain.use_case.DeleteTransactionUseCase
import com.ash.flowr.domain.use_case.UpsertTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuickAddViewModel @Inject constructor(
    private val upsertUseCase: UpsertTransactionUseCase,
    private val deleteUseCase: DeleteTransactionUseCase,
    bankAccountRepo: BankAccountRepository,
    prefs: UserPreferencesRepository
) : ViewModel() {

    val accounts: StateFlow<List<BankAccountEntity>> = bankAccountRepo.observeAll()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val lastUsedAccountId: StateFlow<Long> = prefs.lastUsedAccountId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), -1L)

    fun save(transaction: TransactionEntity, onDone: () -> Unit) {
        viewModelScope.launch {
            upsertUseCase(transaction)
            onDone()
        }
    }

    fun delete(id: Long, onDone: () -> Unit) {
        viewModelScope.launch {
            deleteUseCase(id)
            onDone()
        }
    }
}
