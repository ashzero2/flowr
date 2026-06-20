package com.ash.flowr.ui.sheet.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ash.flowr.data.datastore.UserPreferencesRepository
import com.ash.flowr.data.local.entity.BankAccountEntity
import com.ash.flowr.data.repository.BankAccountRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val bankAccountRepository: BankAccountRepository,
    private val prefs: UserPreferencesRepository
) : ViewModel() {

    var step by mutableStateOf(0)
        private set

    var smsDeclined by mutableStateOf(false)
        private set

    val bankForms = mutableStateListOf(
        BankFormState(colorHex = "#1A56DB"),
        BankFormState(colorHex = "#10B981"),
        BankFormState(colorHex = "#F59E0B")
    )

    val recurringForms = mutableStateListOf(
        RecurringFormState(name = "Rent", isChecked = false),
        RecurringFormState(name = "Netflix", isChecked = false),
        RecurringFormState(name = "Claude", isChecked = false)
    )

    fun onSmsDeclined() { smsDeclined = true }
    fun nextStep() { if (step < 2) step++ }
    fun prevStep() { if (step > 0) step-- }

    fun completeOnboarding(onDone: () -> Unit) = viewModelScope.launch {
        val accounts = bankForms
            .filter { it.name.isNotBlank() }
            .mapIndexed { idx, form ->
                BankAccountEntity(
                    name = form.name,
                    bankName = form.bankName,
                    smsIdentifier = form.smsIdentifier,
                    accountLast4 = form.accountLast4,
                    startingBalance = form.startingBalance.toDoubleOrNull() ?: 0.0,
                    colorHex = form.colorHex,
                    sortOrder = idx
                )
            }
        if (accounts.isNotEmpty()) {
            bankAccountRepository.insertAll(accounts)
        }

        val todayMidnight = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        prefs.completeOnboarding(todayMidnight)
        onDone()
    }
}

data class BankFormState(
    var name: String = "",
    var bankName: String = "",
    var smsIdentifier: String = "",
    var accountLast4: String = "",
    var startingBalance: String = "0",
    var colorHex: String = "#1A56DB"
)

data class RecurringFormState(
    val name: String,
    var isChecked: Boolean = false,
    var amount: String = ""
)
