package com.ash.flowr.ui.screen.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ash.flowr.data.local.entity.BankAccountEntity
import com.ash.flowr.data.local.entity.PendingReviewEntity
import com.ash.flowr.data.local.entity.TransactionEntity
import com.ash.flowr.data.repository.BankAccountRepository
import com.ash.flowr.data.repository.SmsInboxRepository
import com.ash.flowr.domain.use_case.UpsertTransactionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val smsInboxRepository: SmsInboxRepository,
    private val bankAccountRepository: BankAccountRepository,
    private val upsertTransaction: UpsertTransactionUseCase
) : ViewModel() {

    val pending: StateFlow<List<PendingReviewEntity>> = smsInboxRepository.observePending()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val accounts: StateFlow<Map<Long, BankAccountEntity>> = bankAccountRepository.observeAll()
        .map { list -> list.associateBy { it.id } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyMap())

    fun confirm(entity: PendingReviewEntity) = viewModelScope.launch {
        val txn = TransactionEntity(
            amount = entity.amount,
            type = entity.direction,
            category = null,
            bankAccountId = entity.bankAccountId,
            toBankAccountId = null,
            note = entity.merchantName ?: entity.vpa,
            date = entity.date,
            source = "SMS",
            smsId = entity.smsId
        )
        upsertTransaction(txn)
        smsInboxRepository.confirmAndDelete(entity.smsId)
    }

    fun skip(entity: PendingReviewEntity) = viewModelScope.launch {
        smsInboxRepository.skipAndDelete(entity.smsId)
    }
}
