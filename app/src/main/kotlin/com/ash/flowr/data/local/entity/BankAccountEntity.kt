package com.ash.flowr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_accounts")
data class BankAccountEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val bankName: String,
    val smsIdentifier: String,  // Sender ID e.g. VM-HDFCBK — fallback account match
    val accountLast4: String,   // Last 4 digits from SMS e.g. "6561" — primary account match
    val startingBalance: Double,
    val colorHex: String,       // e.g. "#1A56DB"
    val sortOrder: Int
)
