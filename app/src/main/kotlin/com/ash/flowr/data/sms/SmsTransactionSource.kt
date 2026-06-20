package com.ash.flowr.data.sms

import android.content.Context
import android.net.Uri
import com.ash.flowr.data.local.dao.ProcessedSmsDao
import com.ash.flowr.data.repository.BankAccountRepository
import com.ash.flowr.domain.ParsedTransaction
import com.ash.flowr.domain.TransactionSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmsTransactionSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val bankAccountRepository: BankAccountRepository,
    private val processedSmsDao: ProcessedSmsDao
) : TransactionSource {

    override suspend fun fetch(since: Long, now: Long): List<ParsedTransaction> {
        val accounts = bankAccountRepository.observeAll().first()
        if (accounts.isEmpty()) return emptyList()

        val processedIds = processedSmsDao.getAllIds().toSet()

        val cursor = context.contentResolver.query(
            Uri.parse("content://sms/inbox"),
            arrayOf("_id", "date", "address", "body"),
            "date > ?",
            arrayOf(since.toString()),
            "date DESC"
        ) ?: return emptyList()

        val results = mutableListOf<ParsedTransaction>()

        cursor.use {
            val idCol = it.getColumnIndexOrThrow("_id")
            val dateCol = it.getColumnIndexOrThrow("date")
            val addrCol = it.getColumnIndexOrThrow("address")
            val bodyCol = it.getColumnIndexOrThrow("body")

            while (it.moveToNext()) {
                val smsId = it.getLong(idCol)
                if (smsId in processedIds) continue

                val date = it.getLong(dateCol)
                val address = it.getString(addrCol) ?: ""
                val body = it.getString(bodyCol) ?: ""

                val account = accounts.firstOrNull { acc ->
                    (acc.smsIdentifier.isNotBlank() &&
                        address.contains(acc.smsIdentifier, ignoreCase = true)) ||
                    (acc.accountLast4.isNotBlank() && body.contains(acc.accountLast4))
                } ?: continue

                val parsed = SmsParser.parse(body)
                if (parsed.amount == null || parsed.direction == null) continue
                if (parsed.amount <= 0) continue

                results.add(
                    ParsedTransaction(
                        amount = parsed.amount,
                        direction = parsed.direction,
                        vpa = parsed.vpa,
                        merchantName = parsed.merchantName,
                        bankAccountId = account.id,
                        smsId = smsId,
                        date = date,
                        refNo = parsed.referenceNo,
                        rawBody = body,
                        senderAddress = address
                    )
                )
            }
        }

        return results
    }
}
