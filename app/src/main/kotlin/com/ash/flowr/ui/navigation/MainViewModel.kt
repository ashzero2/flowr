package com.ash.flowr.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ash.flowr.data.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val prefs: UserPreferencesRepository
) : ViewModel() {

    // null = loading, false = show onboarding, true = show main app
    val onboardingComplete: StateFlow<Boolean?> = prefs.onboardingComplete
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    fun onOnboardingComplete() {
        // DataStore already updated by OnboardingViewModel; Flow reacts automatically
    }
}
