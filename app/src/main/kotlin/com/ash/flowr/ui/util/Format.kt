package com.ash.flowr.ui.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun Double.toRupees(decimals: Boolean = false): String =
    if (decimals) "₹%.2f".format(this) else "₹%.0f".format(this)

fun Long.toShortDate(): String =
    Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(DateTimeFormatter.ofPattern("d MMM"))
