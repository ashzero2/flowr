package com.ash.flowr.ui.screen.settings

import androidx.lifecycle.ViewModel
import com.ash.flowr.data.datastore.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    val prefs: UserPreferencesRepository
) : ViewModel()
