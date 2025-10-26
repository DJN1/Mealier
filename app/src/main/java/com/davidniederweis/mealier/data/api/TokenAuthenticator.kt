package com.davidniederweis.mealier.data.api

import com.davidniederweis.mealier.data.security.SecureDataStoreManager
import com.davidniederweis.mealier.util.Logger
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val tokenManager: SecureDataStoreManager,
    private val authApi: AuthApi
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.request.url.toString().contains("/api/auth/token")) {
            return null // Do not try to refresh token for token endpoint
        }

        if (response.priorResponse != null) {
            return null // Give up if we've already tried to authenticate.
        }

        synchronized(this) {
            val tokenBeforeRefresh = runBlocking { tokenManager.getTokenSync() }

            // Check if the token was refreshed by another thread while we were waiting.
            if (response.request.header("Authorization") != "Bearer $tokenBeforeRefresh") {
                val newAuthToken = runBlocking { tokenManager.getTokenSync() }
                if (newAuthToken != null) {
                    return response.request.newBuilder()
                        .header("Authorization", "Bearer $newAuthToken")
                        .build()
                }
            }

            return runBlocking {
                val username = tokenManager.getUsername()
                val password = tokenManager.getPassword()

                if (username.isNullOrBlank() || password.isNullOrBlank()) {
                    Logger.w("TokenAuthenticator", "Username or password not available for token refresh.")
                    tokenManager.clearToken()
                    return@runBlocking null
                }

                try {
                    Logger.i("TokenAuthenticator", "Attempting token refresh.")
                    val tokenResponse = authApi.login(username, password)
                    tokenManager.saveToken(tokenResponse.accessToken)
                    Logger.i("TokenAuthenticator", "Token refresh successful.")
                    response.request.newBuilder()
                        .header("Authorization", "Bearer ${tokenResponse.accessToken}")
                        .build()
                } catch (e: Exception) {
                    Logger.e("TokenAuthenticator", "Token refresh failed.", e)
                    tokenManager.clearToken()
                    null
                }
            }
        }
    }
}
