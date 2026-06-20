package com.ash.flowr.ui.sheet.onboarding

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.ash.flowr.ui.theme.IncomeGreen
import com.ash.flowr.ui.theme.MutedText

private val presetColors = listOf(
    "#1C3D2F", "#2E5F8A", "#6B4A9E", "#B85C2A", "#A8395A", "#2A6B62"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingSheet(
    onComplete: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    ModalBottomSheet(
        onDismissRequest = { /* mandatory — not dismissible */ },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
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

    var smsGranted by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED)
    }
    var notifGranted by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }
    var batteryExempted by remember {
        mutableStateOf(
            context.getSystemService(PowerManager::class.java)
                .isIgnoringBatteryOptimizations(context.packageName)
        )
    }

    LaunchedEffect(Unit) {
        if (smsGranted && notifGranted && batteryExempted) viewModel.nextStep()
    }

    val smsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        smsGranted = granted
        if (!granted) viewModel.onSmsDeclined()
    }
    val notifLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        notifGranted = granted
    }
    val batteryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        batteryExempted = context.getSystemService(PowerManager::class.java)
            .isIgnoringBatteryOptimizations(context.packageName)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .navigationBarsPadding()
    ) {
        Text("Welcome to Flowr", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Grant these permissions so Flowr can automatically read your bank SMS.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        PermissionRow(
            title = "Read SMS",
            description = "Parses bank transaction SMS to auto-fill your expenses.",
            actionLabel = "Grant",
            isGranted = smsGranted,
            onClick = { smsLauncher.launch(Manifest.permission.READ_SMS) }
        )
        Spacer(Modifier.height(12.dp))

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            PermissionRow(
                title = "Notifications",
                description = "Sends a nightly review notification when new transactions are found.",
                actionLabel = "Grant",
                isGranted = notifGranted,
                onClick = { notifLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) }
            )
            Spacer(Modifier.height(12.dp))
        }

        PermissionRow(
            title = "Battery Optimization",
            description = "Some Android phones aggressively kill background apps. This keeps Flowr's sweep running reliably.",
            actionLabel = "Open",
            isGranted = batteryExempted,
            onClick = {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:${context.packageName}")
                }
                batteryLauncher.launch(intent)
            }
        )

        Spacer(Modifier.height(32.dp))
        Button(onClick = { viewModel.nextStep() }, modifier = Modifier.fillMaxWidth()) {
            Text("Continue")
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun PermissionRow(
    title: String,
    description: String,
    actionLabel: String,
    isGranted: Boolean,
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
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.width(8.dp))
        if (isGranted) {
            Text("Granted", style = MaterialTheme.typography.labelMedium, color = IncomeGreen)
        } else {
            TextButton(onClick = onClick) { Text(actionLabel) }
        }
    }
}

@Composable
private fun BanksStep(viewModel: OnboardingViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .navigationBarsPadding()
    ) {
        Text("Set up your bank accounts", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Add as many accounts as you have. You can edit or add more later in Settings.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        viewModel.bankForms.forEachIndexed { idx, form ->
            BankFormCard(
                index = idx,
                total = viewModel.bankForms.size,
                form = form,
                onFormChange = { viewModel.bankForms[idx] = it },
                onRemove = { viewModel.removeBankForm(idx) }
            )
            Spacer(Modifier.height(16.dp))
        }

        TextButton(
            onClick = { viewModel.addBankForm() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(Modifier.width(4.dp))
            Text("Add another account")
        }

        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { viewModel.prevStep() }) { Text("Back") }
            Button(onClick = { viewModel.nextStep() }) { Text("Continue") }
        }
        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun BankFormCard(
    index: Int,
    total: Int,
    form: BankFormState,
    onFormChange: (BankFormState) -> Unit,
    onRemove: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Account ${index + 1}", style = MaterialTheme.typography.labelLarge)
            if (total > 1) {
                IconButton(onClick = onRemove, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(16.dp))
                }
            }
        }
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = form.name,
            onValueChange = { onFormChange(form.copy(name = it)) },
            label = { Text("Account nickname") },
            placeholder = { Text("e.g. HDFC Salary", color = MutedText) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = form.bankName,
            onValueChange = { onFormChange(form.copy(bankName = it)) },
            label = { Text("Bank name") },
            placeholder = { Text("e.g. HDFC", color = MutedText) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = form.accountLast4,
            onValueChange = {
                if (it.length <= 4 && it.all { c -> c.isDigit() })
                    onFormChange(form.copy(accountLast4 = it))
            },
            label = { Text("Last 4 digits of account") },
            placeholder = { Text("e.g. 6561", color = MutedText) },
            supportingText = { Text("Found in bank SMS — e.g. \"A/C XX6561\" → enter 6561") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = form.smsIdentifier,
            onValueChange = { onFormChange(form.copy(smsIdentifier = it)) },
            label = { Text("SMS sender ID (optional)") },
            placeholder = { Text("e.g. VM-HDFCBK", color = MutedText) },
            supportingText = { Text("Fallback only — used when SMS doesn't show account digits. Check your bank SMS header.") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = form.startingBalance,
            onValueChange = { onFormChange(form.copy(startingBalance = it)) },
            label = { Text("Starting balance (₹)") },
            placeholder = { Text("0", color = MutedText) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )
        Spacer(Modifier.height(12.dp))

        Text("Account color", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            presetColors.forEach { hex ->
                val color = runCatching { Color(android.graphics.Color.parseColor(hex)) }.getOrDefault(Color.Gray)
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(color)
                        .then(
                            if (form.colorHex == hex) Modifier.border(2.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
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
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .navigationBarsPadding()
    ) {
        Text("Recurring expenses", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            "Flowr can auto-add these on their due date each month. You can set these up later too.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

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
                    Spacer(Modifier.width(8.dp))
                }
                TextButton(onClick = {
                    viewModel.recurringForms[idx] = rec.copy(isChecked = !rec.isChecked)
                }) {
                    Text(if (rec.isChecked) "Remove" else "Add")
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { viewModel.prevStep() }) { Text("Back") }
            Column(horizontalAlignment = Alignment.End) {
                Button(onClick = { viewModel.completeOnboarding(onComplete) }) { Text("Done") }
                TextButton(onClick = { viewModel.completeOnboarding(onComplete) }) { Text("Skip") }
            }
        }
        Spacer(Modifier.height(8.dp))
    }
}
