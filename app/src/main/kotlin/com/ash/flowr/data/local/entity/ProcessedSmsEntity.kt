package com.ash.flowr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "processed_sms")
data class ProcessedSmsEntity(
    @PrimaryKey val smsId: Long,  // Android SMS message ID — natural dedup key
    val processedAt: Long,        // epoch ms when sweep ran
    val wasImported: Boolean      // true = user confirmed; false = user swiped left (skipped)
)
