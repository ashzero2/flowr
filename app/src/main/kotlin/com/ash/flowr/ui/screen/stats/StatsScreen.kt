package com.ash.flowr.ui.screen.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ash.flowr.ui.screen.dashboard.AccountBalance
import com.ash.flowr.ui.theme.IncomeGreen
import com.ash.flowr.ui.theme.MutedText
import com.ash.flowr.ui.theme.color
import com.ash.flowr.ui.theme.icon
import com.ash.flowr.ui.theme.label
import com.ash.flowr.ui.util.toRupees
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun StatsScreen(viewModel: StatsViewModel = hiltViewModel()) {
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val categoryBreakdown by viewModel.categoryBreakdown.collectAsState()
    val spendVsIncome by viewModel.spendVsIncome.collectAsState()
    val accountBalances by viewModel.accountBalances.collectAsState()

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 24.dp)
        ) {
            // Month picker
            item {
                MonthPicker(
                    month = selectedMonth,
                    onPrevious = viewModel::previousMonth,
                    onNext = viewModel::nextMonth
                )
            }

            // Spend vs Income totals
            item {
                SpendVsIncomeRow(spendVsIncome = spendVsIncome)
                Spacer(Modifier.height(8.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))
            }

            // Category breakdown header
            item {
                Text(
                    text = "By category",
                    style = MaterialTheme.typography.titleSmall,
                    color = MutedText,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(12.dp))
            }

            // Category bars
            if (categoryBreakdown.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No expenses this month", color = MutedText)
                    }
                }
            } else {
                val maxAmount = categoryBreakdown.maxOf { it.total }
                items(categoryBreakdown) { ct ->
                    CategoryBar(
                        categoryTotal = ct,
                        maxAmount = maxAmount,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )
                }
            }

            // Per-account balances
            item {
                Spacer(Modifier.height(16.dp))
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Account balances",
                    style = MaterialTheme.typography.titleSmall,
                    color = MutedText,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(Modifier.height(8.dp))
            }

            items(accountBalances) { ab ->
                AccountBalanceRow(accountBalance = ab)
            }
        }
    }
}

@Composable
private fun MonthPicker(month: YearMonth, onPrevious: () -> Unit, onNext: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(onClick = onPrevious) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous month")
        }
        Text(
            text = month.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.getDefault())),
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(180.dp)
        )
        IconButton(onClick = onNext) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next month")
        }
    }
}

@Composable
private fun SpendVsIncomeRow(spendVsIncome: SpendVsIncome) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = "Spent",
                style = MaterialTheme.typography.labelMedium,
                color = MutedText
            )
            Text(
                text = spendVsIncome.spend.toRupees(),
                style = MaterialTheme.typography.displaySmall
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "Income",
                style = MaterialTheme.typography.labelMedium,
                color = MutedText
            )
            Text(
                text = spendVsIncome.income.toRupees(),
                style = MaterialTheme.typography.displaySmall,
                color = IncomeGreen
            )
        }
    }
}

@Composable
private fun CategoryBar(
    categoryTotal: CategoryTotal,
    maxAmount: Double,
    modifier: Modifier = Modifier
) {
    val cat = categoryTotal.category
    val progress = if (maxAmount > 0) (categoryTotal.total / maxAmount).toFloat().coerceIn(0f, 1f) else 0f

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(cat.color.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = cat.icon,
                contentDescription = null,
                tint = cat.color,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(cat.label, style = MaterialTheme.typography.labelMedium)
            Spacer(Modifier.height(2.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = cat.color,
                trackColor = cat.color.copy(alpha = 0.15f)
            )
        }

        Spacer(Modifier.width(10.dp))

        Text(
            text = categoryTotal.total.toRupees(),
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium)
        )
    }
}

@Composable
private fun AccountBalanceRow(accountBalance: AccountBalance) {
    val account = accountBalance.account
    val accentColor = runCatching {
        Color(android.graphics.Color.parseColor(account.colorHex))
    }.getOrElse { MaterialTheme.colorScheme.primary }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(accentColor)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text = account.name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = accountBalance.balance.toRupees(),
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
