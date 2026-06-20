package com.ash.flowr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_review")
data class PendingReviewEntity(
    @PrimaryKey val smsId: Long,
    val amount: Double,
    val direction: String,       // EXPENSE | INCOME
    val bankAccountId: Long,
    val merchantName: String?,
    val vpa: String?,
    val refNo: String?,
    val date: Long,
    val rawBody: String,
    val senderAddress: String
)
