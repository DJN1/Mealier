package com.davidniederweis.mealier.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.davidniederweis.mealier.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.biometricsDataStore: DataStore<Preferences> by preferencesDataStore(name = "biometrics_preferences")

class BiometricsPreferences(private val context: Context) {

    companion object {
        private val BIOMETRIC_ENABLED = booleanPreferencesKey("biometric_enabled")
    }

    val biometricEnabled: Flow<Boolean> = context.biometricsDataStore.data
        .map { preferences ->
            val enabled = preferences[BIOMETRIC_ENABLED] ?: true // Default enabled
            Logger.d("BiometricsPreferences", "Biometric enabled: $enabled")
            enabled
        }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        Logger.i("BiometricsPreferences", "Setting biometric to: $enabled")
        context.biometricsDataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED] = enabled
        }
    }

}
