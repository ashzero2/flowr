package com.ash.flowr.domain.use_case

import com.ash.flowr.data.datastore.UserPreferencesRepository
import com.ash.flowr.data.local.entity.TransactionEntity
import com.ash.flowr.data.repository.TransactionRepository
import javax.inject.Inject

class UpsertTransactionUseCase @Inject constructor(
    private val repo: TransactionRepository,
    private val prefs: UserPreferencesRepository
) {
    suspend operator fun invoke(transaction: TransactionEntity) {
        if (transaction.id == 0L) {
            repo.insert(transaction)
        } else {
            repo.update(transaction)
        }
        prefs.setLastUsedAccountId(transaction.bankAccountId)
    }
}
