package com.ash.flowr.ui.screen.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ash.flowr.data.local.entity.TransactionEntity
import com.ash.flowr.domain.Category
import com.ash.flowr.domain.TransactionType
import com.ash.flowr.ui.theme.IncomeGreen
import com.ash.flowr.ui.theme.MutedText
import com.ash.flowr.ui.theme.TransferMuted
import com.ash.flowr.ui.theme.color
import com.ash.flowr.ui.theme.icon
import com.ash.flowr.ui.theme.label
import com.ash.flowr.ui.util.toRupees
import com.ash.flowr.ui.util.toShortDate

@Composable
fun DashboardScreen(
    onAddClick: () -> Unit = {},
    onTransactionClick: (TransactionEntity) -> Unit = {},
    onTransactionLongClick: (TransactionEntity) -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val recentTransactions by viewModel.recentTransactions.collectAsState()
    val summary by viewModel.monthSummary.collectAsState()

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddClick,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Add") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (accounts.isEmpty()) {
                        item { EmptyAccountCard() }
                    } else {
                        items(accounts) { accountBalance ->
                            BankAccountCard(accountBalance)
                        }
                    }
                }
            }

            item {
                MonthSpendSection(summary = summary)
                Spacer(Modifier.height(8.dp))
            }

            item {
                Text(
                    text = "Recent",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }

            if (recentTransactions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No transactions yet", color = MutedText)
                    }
                }
            } else {
                items(recentTransactions) { txn ->
                    TransactionRow(
                        transaction = txn,
                        onClick = { onTransactionClick(txn) },
                        onLongClick = { onTransactionLongClick(txn) }
                    )
                }
            }
        }
    }
}

@Composable
private fun BankAccountCard(accountBalance: AccountBalance) {
    val account = accountBalance.account
    val accentColor = runCatching {
        Color(android.graphics.Color.parseColor(account.colorHex))
    }.getOrElse { MaterialTheme.colorScheme.primary }

    Card(
        modifier = Modifier
            .width(196.dp)
            .height(108.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxSize()
                    .background(accentColor)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                    Text(
                        text = account.bankName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
                Text(
                    text = accountBalance.balance.toRupees(decimals = false),
                    style = MaterialTheme.typography.titleLarge,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun EmptyAccountCard() {
    Card(
        modifier = Modifier
            .width(196.dp)
            .height(108.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                "No accounts",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
private fun MonthSpendSection(summary: MonthSummary) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "spent this month",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = summary.totalExpense.toRupees(),
            style = MaterialTheme.typography.displayMedium
        )
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SummaryChip(
                icon = Icons.Default.ArrowUpward,
                label = summary.totalExpense.toRupees(),
                iconTint = MaterialTheme.colorScheme.onSurface
            )
            SummaryChip(
                icon = Icons.Default.ArrowDownward,
                label = summary.totalIncome.toRupees(),
                iconTint = IncomeGreen
            )
            SummaryChip(
                icon = Icons.Default.SwapHoriz,
                label = summary.totalTransfer.toRupees(),
                iconTint = TransferMuted
            )
        }
    }
}

@Composable
private fun SummaryChip(icon: ImageVector, label: String, iconTint: Color) {
    Surface(
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(14.dp), tint = iconTint)
            Text(label, style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val category = transaction.category
        ?.let { runCatching { Category.valueOf(it) }.getOrNull() }
        ?: Category.OTHER
    val type = runCatching { TransactionType.valueOf(transaction.type) }.getOrElse { TransactionType.EXPENSE }

    val (categoryIcon, categoryColor) = when (type) {
        TransactionType.INCOME -> Icons.Default.ArrowDownward to IncomeGreen
        TransactionType.TRANSFER -> Icons.Default.SwapHoriz to TransferMuted
        TransactionType.EXPENSE -> category.icon to category.color
    }

    val amountDisplay = when (type) {
        TransactionType.INCOME -> "+${transaction.amount.toRupees()}"
        TransactionType.EXPENSE -> transaction.amount.toRupees()
        TransactionType.TRANSFER -> transaction.amount.toRupees()
    }

    val amountColor = when (type) {
        TransactionType.INCOME -> IncomeGreen
        TransactionType.TRANSFER -> TransferMuted
        TransactionType.EXPENSE -> MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(categoryColor.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = categoryIcon,
                contentDescription = null,
                tint = categoryColor,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = when (type) {
                    TransactionType.EXPENSE -> transaction.note ?: category.label
                    TransactionType.INCOME -> transaction.note ?: "Income"
                    TransactionType.TRANSFER -> transaction.note ?: "Transfer"
                },
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1
            )
            Text(
                text = transaction.date.toShortDate(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = amountDisplay,
            style = MaterialTheme.typography.bodyLarge,
            color = amountColor
        )
    }
}
