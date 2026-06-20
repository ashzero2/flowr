package com.ash.flowr.ui.sheet.onboarding

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

private val presetColors = listOf(
    "#1A56DB", "#10B981", "#F59E0B", "#EF4444", "#8B5CF6", "#EC4899"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingSheet(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    ModalBottomSheet(
        onDismissRequest = { /* not dismissible — onboarding is mandatory */ },
        sheetState = rememberModalBottomSheetState(),
        dragHandle = null
    ) {
        when (viewModel.step) {
            0 -> PermissionsStep(viewModel)
            1 -> BanksStep(viewModel)
            2 -> RecurringStep(viewModel, onComplete)
        }
    }
}

@Composable
private fun PermissionsStep(viewModel: OnboardingViewModel) {
    val context = LocalContext.current

    val smsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (!granted) viewModel.onSmsDeclined() }

    val notifLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* ignore result — POST_NOTIFICATIONS is best-effort */ }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Welcome to Flowr", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Grant these permissions so Flowr can automatically read your bank SMS.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        PermissionRow(
            title = "Read SMS",
            description = "Parses bank transaction SMS to auto-fill your expenses.",
            action = "Grant",
            onClick = { smsLauncher.launch(Manifest.permission.READ_SMS) }
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionRow(
                title = "Notifications",
                description = "Sends a nightly review notification when new transactions are found.",
                action = "Grant",
                onClick = { notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        PermissionRow(
            title = "Battery Optimization",
            description = "Prevents ColorOS from killing Flowr's background sweep.",
            action = "Open",
            onClick = {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                context.startActivity(intent)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { viewModel.nextStep() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Continue")
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun PermissionRow(
    title: String,
    description: String,
    action: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall)
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = onClick) { Text(action) }
    }
}

@Composable
private fun BanksStep(viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Set up your bank accounts", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Add up to 3 accounts. You can edit them later in Settings.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        viewModel.bankForms.forEachIndexed { idx, form ->
            BankFormCard(
                label = "Account ${idx + 1}",
                form = form,
                onFormChange = { viewModel.bankForms[idx] = it },
                smsDeclined = viewModel.smsDeclined
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { viewModel.prevStep() }) { Text("Back") }
            Button(onClick = { viewModel.nextStep() }) { Text("Continue") }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun BankFormCard(
    label: String,
    form: BankFormState,
    onFormChange: (BankFormState) -> Unit,
    smsDeclined: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = form.name,
            onValueChange = { onFormChange(form.copy(name = it)) },
            label = { Text("Account nickname") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = form.bankName,
            onValueChange = { onFormChange(form.copy(bankName = it)) },
            label = { Text("Bank name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = form.accountLast4,
            onValueChange = { if (it.length <= 4 && it.all { c -> c.isDigit() }) onFormChange(form.copy(accountLast4 = it)) },
            label = { Text("Last 4 digits (e.g. 6561)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = form.smsIdentifier,
            onValueChange = { onFormChange(form.copy(smsIdentifier = it)) },
            label = { Text(if (smsDeclined) "SMS sender ID (e.g. VM-HDFCBK)" else "SMS sender ID") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = form.startingBalance,
            onValueChange = { onFormChange(form.copy(startingBalance = it)) },
            label = { Text("Starting balance (₹)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(modifier = Modifier.height(12.dp))

        Text("Account color", style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            presetColors.forEach { hex ->
                val color = runCatching { Color(android.graphics.Color.parseColor(hex)) }
                    .getOrDefault(Color.Gray)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color)
                        .then(
                            if (form.colorHex == hex)
                                Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                            else Modifier
                        )
                        .clickable { onFormChange(form.copy(colorHex = hex)) }
                )
            }
        }
    }
}

@Composable
private fun RecurringStep(viewModel: OnboardingViewModel, onComplete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Recurring expenses", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Flowr can auto-add these on their due date each month. You can add more later.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))

        viewModel.recurringForms.forEachIndexed { idx, rec ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(rec.name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                if (rec.isChecked) {
                    OutlinedTextField(
                        value = rec.amount,
                        onValueChange = { viewModel.recurringForms[idx] = rec.copy(amount = it) },
                        label = { Text("₹") },
                        modifier = Modifier.width(100.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                TextButton(onClick = {
                    viewModel.recurringForms[idx] = rec.copy(isChecked = !rec.isChecked)
                }) {
                    Text(if (rec.isChecked) "Remove" else "Add")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { viewModel.prevStep() }) { Text("Back") }
            Column(horizontalAlignment = Alignment.End) {
                Button(onClick = { viewModel.completeOnboarding(onComplete) }) {
                    Text("Done")
                }
                TextButton(onClick = { viewModel.completeOnboarding(onComplete) }) {
                    Text("Skip")
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
