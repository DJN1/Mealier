package com.davidniederweis.mealier.data.security

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.davidniederweis.mealier.util.Logger
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "secure_prefs")

class SecureDataStoreManager(private val context: Context) {

    private val aead: Aead? by lazy {
        initializeTink()
    }

    private fun initializeTink(): Aead? {
        return try {
            // Register Tink configuration
            AeadConfig.register()

            // Create or retrieve keyset
            val keysetHandle = AndroidKeysetManager.Builder()
                .withSharedPref(context, KEYSET_NAME, KEYSET_PREF_NAME)
                .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withMasterKeyUri(MASTER_KEY_URI)
                .build()
                .keysetHandle

            Logger.i("SecureDataStore", "Tink initialized successfully")
            keysetHandle.getPrimitive(Aead::class.java)
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Failed to initialize Tink, attempting recovery", e)
            
            // Try to recover by clearing corrupted keyset and recreating
            try {
                Logger.i("SecureDataStore", "Clearing corrupted keyset and recreating")
                context.getSharedPreferences(KEYSET_PREF_NAME, android.content.Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()
                
                // Try again with fresh keyset
                AeadConfig.register()
                val keysetHandle = AndroidKeysetManager.Builder()
                    .withSharedPref(context, KEYSET_NAME, KEYSET_PREF_NAME)
                    .withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                    .withMasterKeyUri(MASTER_KEY_URI)
                    .build()
                    .keysetHandle
                
                Logger.i("SecureDataStore", "Tink recovery successful")
                keysetHandle.getPrimitive(Aead::class.java)
            } catch (recoveryException: Exception) {
                Logger.e("SecureDataStore", "Tink recovery failed", recoveryException)
                null
            }
        }
    }

    private fun encrypt(plaintext: String): String {
        return try {
            val aeadInstance = aead
            if (aeadInstance == null) {
                Logger.e("SecureDataStore", "Cannot encrypt: Tink not initialized")
                throw IllegalStateException("Tink not initialized")
            }
            val ciphertext = aeadInstance.encrypt(plaintext.toByteArray(), null)
            android.util.Base64.encodeToString(ciphertext, android.util.Base64.DEFAULT)
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Encryption failed", e)
            throw e
        }
    }

    private fun decrypt(ciphertext: String): String {
        return try {
            val aeadInstance = aead
            if (aeadInstance == null) {
                Logger.w("SecureDataStore", "Cannot decrypt: Tink not initialized")
                throw IllegalStateException("Tink not initialized")
            }
            val decoded = android.util.Base64.decode(ciphertext, android.util.Base64.DEFAULT)
            val plaintext = aeadInstance.decrypt(decoded, null)
            String(plaintext)
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Decryption failed", e)
            throw e
        }
    }

    // Token Operations
    suspend fun saveToken(token: String) {
        try {
            val encrypted = encrypt(token)
            context.dataStore.edit { prefs ->
                prefs[KEY_TOKEN] = encrypted
            }
            Logger.i("SecureDataStore", "Token saved successfully")
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Failed to save token", e)
        }
    }

    fun getToken(): Flow<String?> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Logger.e("SecureDataStore", "Error reading token", exception)
                emit(androidx.datastore.preferences.core.emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            prefs[KEY_TOKEN]?.let { encrypted ->
                try {
                    decrypt(encrypted)
                } catch (e: Exception) {
                    Logger.e("SecureDataStore", "Failed to decrypt token", e)
                    null
                }
            }
        }

    suspend fun getTokenOnce(): String? {
        return try {
            getToken().first()
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Failed to get token", e)
            null
        }
    }

    fun getTokenSync(): String? {
        return try {
            kotlinx.coroutines.runBlocking {
                getTokenOnce()
            }
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Failed to get token synchronously", e)
            null
        }
    }


    suspend fun clearToken() {
        try {
            context.dataStore.edit { prefs ->
                prefs.remove(KEY_TOKEN)
            }
            Logger.i("SecureDataStore", "Token cleared successfully")
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Failed to clear token", e)
        }
    }

    suspend fun hasToken(): Boolean {
        return try {
            getTokenOnce() != null
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Failed to check token existence", e)
            false
        }
    }

    // Credentials Operations
    suspend fun saveCredentials(username: String, password: String) {
        try {
            val encryptedUsername = encrypt(username)
            val encryptedPassword = encrypt(password)
            context.dataStore.edit { prefs ->
                prefs[KEY_USERNAME] = encryptedUsername
                prefs[KEY_PASSWORD] = encryptedPassword
            }
            Logger.i("SecureDataStore", "Credentials saved successfully")
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Failed to save credentials", e)
        }
    }

    suspend fun getUsername(): String? {
        return try {
            val prefs = context.dataStore.data.first()
            prefs[KEY_USERNAME]?.let { decrypt(it) }
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Failed to get username", e)
            null
        }
    }

    suspend fun getPassword(): String? {
        return try {
            val prefs = context.dataStore.data.first()
            prefs[KEY_PASSWORD]?.let { decrypt(it) }
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Failed to get password", e)
            null
        }
    }

    suspend fun clearCredentials() {
        try {
            context.dataStore.edit { prefs ->
                prefs.remove(KEY_USERNAME)
                prefs.remove(KEY_PASSWORD)
            }
            Logger.i("SecureDataStore", "Credentials cleared successfully")
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Failed to clear credentials", e)
        }
    }

    // Clear All Data
    suspend fun clearAll() {
        try {
            context.dataStore.edit { prefs ->
                prefs.clear()
            }
            Logger.i("SecureDataStore", "All data cleared successfully")
        } catch (e: Exception) {
            Logger.e("SecureDataStore", "Failed to clear all data", e)
        }
    }

    companion object {
        private const val KEYSET_NAME = "master_keyset"
        private const val KEYSET_PREF_NAME = "master_key_prefs"
        private const val MASTER_KEY_URI = "android-keystore://master_key"

        private val KEY_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_USERNAME = stringPreferencesKey("username")
        private val KEY_PASSWORD = stringPreferencesKey("password")
    }
}
