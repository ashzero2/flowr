package com.ash.flowr.domain

interface TransactionSource {
    suspend fun fetch(since: Long, now: Long): List<ParsedTransaction>
}
