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

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "theme_preferences")

class ThemePreferences(private val context: Context) {

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            val isDark = preferences[DARK_MODE_KEY] ?: false
            Logger.d("ThemePreferences", "Dark mode: $isDark")
            isDark
        }

    suspend fun setDarkMode(enabled: Boolean) {
        Logger.i("ThemePreferences", "Setting dark mode to: $enabled")
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun toggleDarkMode() {
        context.dataStore.edit { preferences ->
            val current = preferences[DARK_MODE_KEY] ?: false
            preferences[DARK_MODE_KEY] = !current
            Logger.i("ThemePreferences", "Toggled dark mode from $current to ${!current}")
        }
    }
}