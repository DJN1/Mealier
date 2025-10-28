package com.davidniederweis.mealier.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.davidniederweis.mealier.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.serverDataStore: DataStore<Preferences> by preferencesDataStore(name = "server_preferences")

class ServerPreferences(private val context: Context) {

    companion object {
        private val SERVER_URL = stringPreferencesKey("server_url")
        const val DEFAULT_SERVER_URL = "" // Empty by default, user must provide
    }

    val serverUrl: Flow<String> = context.serverDataStore.data
        .map { preferences ->
            val url = preferences[SERVER_URL] ?: DEFAULT_SERVER_URL
            Logger.d("ServerPreferences", "Server URL: $url")
            url
        }

    suspend fun setServerUrl(url: String) {
        Logger.i("ServerPreferences", "Setting server URL to: $url")
        context.serverDataStore.edit { preferences ->
            // Remove trailing slash if present
            val cleanUrl = url.trimEnd('/')
            preferences[SERVER_URL] = cleanUrl
        }
    }

    suspend fun getServerUrlOnce(): String {
        return serverUrl.first()
    }

}
