package com.ash.flowr.domain.use_case

import com.ash.flowr.data.repository.TransactionRepository
import javax.inject.Inject

class DeleteTransactionUseCase @Inject constructor(
    private val repo: TransactionRepository
) {
    suspend operator fun invoke(id: Long) = repo.deleteById(id)
}
