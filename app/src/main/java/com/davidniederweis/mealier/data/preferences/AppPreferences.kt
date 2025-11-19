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

private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferences(private val context: Context) {

    companion object {
        private val KEEP_SCREEN_ON_KEY = booleanPreferencesKey("keep_screen_on")
    }

    val keepScreenOn: Flow<Boolean> = context.appDataStore.data
        .map { preferences ->
            val keepOn = preferences[KEEP_SCREEN_ON_KEY] ?: false
            Logger.d("AppPreferences", "Keep screen on: $keepOn")
            keepOn
        }

    suspend fun setKeepScreenOn(enabled: Boolean) {
        Logger.i("AppPreferences", "Setting keep screen on to: $enabled")
        context.appDataStore.edit { preferences ->
            preferences[KEEP_SCREEN_ON_KEY] = enabled
        }
    }
}
