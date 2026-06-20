package com.ash.flowr.ui.screen.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ash.flowr.domain.TransactionType
import com.ash.flowr.ui.theme.IncomeGreen
import com.ash.flowr.ui.theme.MutedText
import com.ash.flowr.ui.util.toRupees
import com.ash.flowr.ui.util.toShortDate

@Composable
fun ReviewScreen(viewModel: ReviewViewModel = hiltViewModel()) {
    val pending by viewModel.pending.collectAsState()
    val accounts by viewModel.accounts.collectAsState()

    Scaffold { innerPadding ->
        if (pending.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "All caught up",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "No new transactions from SMS",
                        style = MaterialTheme.typography.bodySmall,
                        color = MutedText
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "${pending.size} to review",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                items(pending, key = { it.smsId }) { entity ->
                    val account = accounts[entity.bankAccountId]
                    val accentColor = account?.colorHex?.let {
                        runCatching { Color(android.graphics.Color.parseColor(it)) }.getOrNull()
                    } ?: MaterialTheme.colorScheme.primary

                    val isExpense = entity.direction == TransactionType.EXPENSE.name
                    val amountColor = if (isExpense) MaterialTheme.colorScheme.onSurface else IncomeGreen
                    val directionIcon = if (isExpense) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
                    val directionTint = if (isExpense) MaterialTheme.colorScheme.onSurfaceVariant else IncomeGreen

                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(0.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(modifier = Modifier.fillMaxWidth()) {
                            // Account color strip
                            Box(
                                modifier = Modifier
                                    .width(4.dp)
                                    .height(140.dp)
                                    .background(accentColor)
                            )
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                // Amount + direction icon
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = entity.amount.toRupees(),
                                        style = MaterialTheme.typography.displaySmall,
                                        color = amountColor
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(directionTint.copy(alpha = 0.10f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            directionIcon,
                                            contentDescription = null,
                                            tint = directionTint,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(Modifier.height(4.dp))

                                // Merchant / VPA
                                val label = entity.merchantName ?: entity.vpa ?: entity.senderAddress
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                // Account name + date
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = account?.name ?: "Unknown account",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = entity.date.toShortDate(),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                // Raw SMS snippet
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    text = entity.rawBody,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(Modifier.height(10.dp))

                                // Action row
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    FilledTonalButton(
                                        onClick = { viewModel.confirm(entity) },
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 8.dp)
                                    ) {
                                        Icon(Icons.Default.Check, null, Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Add")
                                    }
                                    OutlinedButton(
                                        onClick = { viewModel.skip(entity) },
                                        modifier = Modifier.weight(1f),
                                        contentPadding = PaddingValues(vertical = 8.dp),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                        colors = ButtonDefaults.outlinedButtonColors(
                                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    ) {
                                        Icon(Icons.Default.Close, null, Modifier.size(16.dp))
                                        Spacer(Modifier.width(4.dp))
                                        Text("Skip")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
