package com.ash.flowr.data.sms

import com.ash.flowr.domain.TransactionType

object SmsParser {

    data class ParseResult(
        val amount: Double?,
        val direction: TransactionType?,
        val accountLast4: String?,
        val merchantName: String?,
        val vpa: String?,
        val referenceNo: String?
    )

    fun parse(raw: String): ParseResult {
        val tokens = tokenize(raw)
        return ParseResult(
            amount = extractAmount(tokens),
            direction = extractDirection(raw),
            accountLast4 = extractAccountLast4(tokens),
            merchantName = extractMerchant(raw, tokens),
            vpa = extractVpa(tokens),
            referenceNo = extractRefNo(raw)
        )
    }

    // Mirrors processMessage() from the TS parser
    private fun tokenize(raw: String): List<String> {
        var s = raw.lowercase()
        s = s.replace("!", "")
        s = s.replace(":", " ")
        s = s.replace("/", "")
        s = s.replace("=", " ")
        s = s.replace(Regex("[{}]"), " ")
        s = s.replace("\n", " ").replace("\r", " ")
        s = s.replace("ending ", "")
        s = s.replace(Regex("[x*]"), "")
        s = s.replace("is ", "")
        s = s.replace("with ", "")
        s = s.replace("no. ", "")
        s = s.replace(Regex("\\bac\\b|\\bacct\\b|\\baccount\\b"), "ac")
        // normalize Rs — must come before splitting
        s = s.replace(Regex("rs(?=\\w)"), "rs. ")
        s = s.replace("rs ", "rs. ")
        s = s.replace(Regex("inr(?=\\w)"), "rs. ")
        s = s.replace("inr ", "rs. ")
        s = s.replace("rs. ", "rs.")
        s = s.replace(Regex("rs\\.(?=\\w)"), "rs. ")
        s = s.replace("debited", " debited ")
        s = s.replace("credited", " credited ")
        return s.split(Regex("\\s+")).filter { it.isNotEmpty() }
    }

    private fun extractAmount(tokens: List<String>): Double? {
        val rsIndex = tokens.indexOfFirst { it == "rs." }
        if (rsIndex == -1) return null
        val raw = tokens.getOrNull(rsIndex + 1)?.replace(",", "") ?: return null
        if (raw.toDoubleOrNull() != null) return raw.toDouble()
        // false positive — look one ahead
        val raw2 = tokens.getOrNull(rsIndex + 2)?.replace(",", "") ?: return null
        return raw2.toDoubleOrNull()
    }

    private fun extractDirection(raw: String): TransactionType? {
        val lower = raw.lowercase()
        val debitPattern = Regex(
            "debited|debit|deducted|payment|spent|paid|used at|charged|transaction fee|" +
            "tran |booked|purchased|sent to|purchase of|spent on"
        )
        val creditPattern = Regex("credited|credit|deposited|added|received|refund|repayment")
        return when {
            debitPattern.containsMatchIn(lower) -> TransactionType.EXPENSE
            creditPattern.containsMatchIn(lower) -> TransactionType.INCOME
            else -> null
        }
    }

    private fun extractAccountLast4(tokens: List<String>): String? {
        for (i in tokens.indices) {
            val token = tokens[i]
            if (token == "ac") {
                val next = tokens.getOrNull(i + 1) ?: continue
                val trimmed = next.trimStart { !it.isDigit() }.trimEnd { !it.isDigit() }
                if (trimmed.all { it.isDigit() } && trimmed.isNotEmpty()) {
                    return trimmed.takeLast(4)
                }
            } else if (token.contains("ac") && token != "each") {
                val digits = token.replace("ac", "").filter { it.isDigit() }
                if (digits.length >= 4) return digits.takeLast(4)
            }
        }
        return null
    }

    private fun extractVpa(tokens: List<String>): String? {
        val idx = tokens.indexOfFirst { it == "vpa" }
        if (idx == -1 || idx >= tokens.size - 1) return null
        return tokens[idx + 1].replace(Regex("[()]"), "").split(" ").firstOrNull()
    }

    private fun extractMerchant(raw: String, tokens: List<String>): String? {
        val vpa = extractVpa(tokens)
        if (vpa != null) {
            return vpa.split("@").firstOrNull()
        }
        // Fallback: "at <Merchant>" or "to <Merchant>" patterns
        val atMatch = Regex(
            "(?:at|to)\\s+([A-Za-z][A-Za-z0-9 .&'-]{2,30}?)(?:\\s*[.,]|\\s+on\\b|\\s+for\\b|\\s+via\\b|\$)",
            RegexOption.IGNORE_CASE
        ).find(raw)
        return atMatch?.groupValues?.getOrNull(1)?.trim()
    }

    private fun extractRefNo(raw: String): String? {
        val lower = raw.lowercase()
        val keywords = listOf("upi ref no", "upi ref", "ref no.", "refno", "transaction id", "txn id", "imps ref", "ref")
        for (keyword in keywords) {
            val idx = lower.indexOf(keyword)
            if (idx != -1) {
                val after = raw.substring(idx + keyword.length).trimStart(':', ' ', '.')
                val refNo = after.split(Regex("[^0-9]")).firstOrNull { it.length >= 6 }
                if (refNo != null) return refNo
            }
        }
        return null
    }
}
