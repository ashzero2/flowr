package com.ash.flowr.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "holdings")
data class HoldingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val symbol: String,
    val broker: String,        // KITE | GROWW | OTHER
    val avgBuy: Double,
    val qty: Double,           // Double — MF/ETF units are fractional (e.g. 12.543)
    val currentPrice: Double,
    val lastUpdated: Long      // epoch ms; shown in UI so user knows how stale the price is
)
