package com.ash.flowr.data.datastore

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<UserPreferences>
) {
    val lastSuccessfulSweep: Flow<Long> = dataStore.data.map { it.lastSuccessfulSweep }
    val portfolioEnabled: Flow<Boolean> = dataStore.data.map { it.portfolioEnabled }
    val themeMode: Flow<ThemeMode> = dataStore.data.map { it.themeMode }
    val materialYouEnabled: Flow<Boolean> = dataStore.data.map { it.materialYouEnabled }
    val onboardingComplete: Flow<Boolean> = dataStore.data.map { it.onboardingComplete }
    val lastUsedAccountId: Flow<Long> = dataStore.data.map { it.lastUsedAccountId }

    suspend fun setLastSuccessfulSweep(epochMs: Long) {
        dataStore.updateData { it.toBuilder().setLastSuccessfulSweep(epochMs).build() }
    }

    suspend fun setPortfolioEnabled(enabled: Boolean) {
        dataStore.updateData { it.toBuilder().setPortfolioEnabled(enabled).build() }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        dataStore.updateData { it.toBuilder().setThemeMode(mode).build() }
    }

    suspend fun setMaterialYouEnabled(enabled: Boolean) {
        dataStore.updateData { it.toBuilder().setMaterialYouEnabled(enabled).build() }
    }

    suspend fun setLastUsedAccountId(id: Long) {
        dataStore.updateData { it.toBuilder().setLastUsedAccountId(id).build() }
    }

    suspend fun completeOnboarding(todayMidnightEpochMs: Long) {
        dataStore.updateData {
            it.toBuilder()
                .setOnboardingComplete(true)
                .setLastSuccessfulSweep(todayMidnightEpochMs)
                .build()
        }
    }
}
