package com.ash.flowr.ui.sheet.quickadd

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ash.flowr.data.local.entity.TransactionEntity
import com.ash.flowr.domain.Category
import com.ash.flowr.domain.TransactionType
import com.ash.flowr.ui.theme.color
import com.ash.flowr.ui.theme.icon
import com.ash.flowr.ui.theme.label

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAddSheet(
    editingTransaction: TransactionEntity? = null,
    onDismiss: () -> Unit,
    viewModel: QuickAddViewModel = hiltViewModel()
) {
    val accounts by viewModel.accounts.collectAsState()
    val lastUsedId by viewModel.lastUsedAccountId.collectAsState()

    val prefillAccountId = editingTransaction?.bankAccountId
        ?: if (lastUsedId > 0) lastUsedId else accounts.firstOrNull()?.id ?: -1L

    var amountText by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.amount?.let { "%.2f".format(it) } ?: "")
    }
    var type by remember(editingTransaction) {
        mutableStateOf(
            editingTransaction?.let {
                runCatching { TransactionType.valueOf(it.type) }.getOrElse { TransactionType.EXPENSE }
            } ?: TransactionType.EXPENSE
        )
    }
    var category by remember(editingTransaction) {
        mutableStateOf(
            editingTransaction?.category
                ?.let { runCatching { Category.valueOf(it) }.getOrNull() }
                ?: Category.FOOD
        )
    }
    var selectedAccountId by remember(editingTransaction, prefillAccountId) {
        mutableStateOf(editingTransaction?.bankAccountId ?: prefillAccountId)
    }
    var toAccountId by remember(editingTransaction) {
        mutableStateOf(editingTransaction?.toBankAccountId ?: -1L)
    }
    var note by remember(editingTransaction) { mutableStateOf(editingTransaction?.note ?: "") }
    var noteExpanded by remember { mutableStateOf(editingTransaction?.note?.isNotBlank() == true) }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    val canSave = amountText.toDoubleOrNull() != null &&
            amountText.isNotBlank() &&
            selectedAccountId > 0 &&
            (type != TransactionType.TRANSFER || toAccountId > 0)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(confirmValueChange = { true })
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .navigationBarsPadding()
                .imePadding()
        ) {
            Text(
                text = if (editingTransaction != null) "Edit transaction" else "Add transaction",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(16.dp))

            // Amount
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount") },
                prefix = { Text("₹") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )

            Spacer(Modifier.height(12.dp))

            // Type chips
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TransactionType.entries.forEach { t ->
                    FilterChip(
                        selected = type == t,
                        onClick = { type = t },
                        label = { Text(t.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        leadingIcon = if (type == t) {
                            { Icon(Icons.Default.Check, null, Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Category chips (EXPENSE only)
            if (type == TransactionType.EXPENSE) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Category.entries.forEach { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.label) },
                            leadingIcon = {
                                Icon(
                                    cat.icon,
                                    contentDescription = null,
                                    tint = cat.color,
                                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                                )
                            }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            // Bank account selection (from account)
            Text(
                text = if (type == TransactionType.TRANSFER) "From account" else "Account",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                accounts.forEach { account ->
                    FilterChip(
                        selected = selectedAccountId == account.id,
                        onClick = { selectedAccountId = account.id },
                        label = { Text(account.name, maxLines = 1) },
                        leadingIcon = if (selectedAccountId == account.id) {
                            { Icon(Icons.Default.Check, null, Modifier.size(FilterChipDefaults.IconSize)) }
                        } else null
                    )
                }
            }

            // To account (TRANSFER only)
            if (type == TransactionType.TRANSFER) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "To account",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    accounts.filter { it.id != selectedAccountId }.forEach { account ->
                        FilterChip(
                            selected = toAccountId == account.id,
                            onClick = { toAccountId = account.id },
                            label = { Text(account.name, maxLines = 1) },
                            leadingIcon = if (toAccountId == account.id) {
                                { Icon(Icons.Default.Check, null, Modifier.size(FilterChipDefaults.IconSize)) }
                            } else null
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Note (collapsible)
            TextButton(
                onClick = { noteExpanded = !noteExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row {
                    Text(if (noteExpanded) "Hide note" else "Add note")
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        if (noteExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null
                    )
                }
            }

            if (noteExpanded) {
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val amount = amountText.toDoubleOrNull() ?: return@Button
                    val txn = TransactionEntity(
                        id = editingTransaction?.id ?: 0L,
                        amount = amount,
                        type = type.name,
                        category = if (type == TransactionType.EXPENSE) category.name else null,
                        bankAccountId = selectedAccountId,
                        toBankAccountId = if (type == TransactionType.TRANSFER) toAccountId else null,
                        note = note.ifBlank { null },
                        date = editingTransaction?.date ?: System.currentTimeMillis(),
                        source = editingTransaction?.source ?: "MANUAL",
                        smsId = editingTransaction?.smsId
                    )
                    viewModel.save(txn, onDone = onDismiss)
                },
                enabled = canSave,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (editingTransaction != null) "Update" else "Save")
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
