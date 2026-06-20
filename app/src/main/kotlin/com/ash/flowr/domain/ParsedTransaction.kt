package com.ash.flowr.domain

data class ParsedTransaction(
    val amount: Double,
    val direction: TransactionType,
    val vpa: String?,
    val merchantName: String?,
    val bankAccountId: Long,
    val smsId: Long,
    val date: Long,
    val refNo: String?,
    val rawBody: String,
    val senderAddress: String
)
