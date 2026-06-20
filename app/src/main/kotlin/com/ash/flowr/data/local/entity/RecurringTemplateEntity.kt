package com.ash.flowr.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "recurring_templates",
    foreignKeys = [
        ForeignKey(
            entity = BankAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["bankAccountId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("bankAccountId")]
)
data class RecurringTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val amount: Double,
    val category: String,
    val bankAccountId: Long,
    val dayOfMonth: Int,  // 1–28; capped at 28 to avoid Feb and 30-day month edge cases
    val nextDue: Long,    // epoch ms of next scheduled execution
    val isActive: Boolean
)
