package com.ash.flowr.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    foreignKeys = [
        ForeignKey(
            entity = BankAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["bankAccountId"],
            onDelete = ForeignKey.RESTRICT
        ),
        ForeignKey(
            entity = BankAccountEntity::class,
            parentColumns = ["id"],
            childColumns = ["toBankAccountId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("bankAccountId"), Index("toBankAccountId")]
)
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val type: String,           // EXPENSE | INCOME | TRANSFER
    val category: String?,      // null for INCOME and TRANSFER
    val bankAccountId: Long,
    val toBankAccountId: Long?, // TRANSFER only — destination account
    val note: String?,
    val date: Long,             // epoch ms
    val source: String,         // MANUAL | SMS | RECURRING
    val smsId: Long?            // Android SMS message ID; non-null only when source = SMS
)
