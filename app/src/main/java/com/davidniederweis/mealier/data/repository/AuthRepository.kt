package com.davidniederweis.mealier.data.repository

import com.davidniederweis.mealier.data.api.AuthApi
import com.davidniederweis.mealier.data.model.user.UserProfile
import com.davidniederweis.mealier.data.security.SecureDataStoreManager
import com.davidniederweis.mealier.util.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException

class AuthRepository(
    private val authApi: AuthApi,
    private val tokenManager: SecureDataStoreManager
) {
    fun login(username: String, password: String): Flow<Result<UserProfile>> = flow {
        emit(Result.Loading)
        try {
            Logger.d("AuthRepository", "Attempting login for user: $username")

            val tokenResponse = authApi.login(username, password)
            Logger.i("AuthRepository", "Login successful, token received")

            tokenManager.saveToken(tokenResponse.accessToken)

            // Save credentials for biometric login if needed
            tokenManager.saveCredentials(username, password)

            // Token is now saved, interceptor will add it automatically
            val user = authApi.getCurrentUser()
            Logger.i("AuthRepository", "User fetched successfully: ${user.username}")

            emit(Result.Success(user))
        } catch (e: Exception) {
            Logger.e("AuthRepository", "Login error", e)
            emit(Result.Error(e.message ?: "Login failed"))
        }
    }

    suspend fun isLoggedIn(): Boolean {
        return tokenManager.hasToken()
    }

    suspend fun logout() {
        tokenManager.clearToken()
        tokenManager.clearCredentials()
    }

    fun getCurrentUser(): Flow<Result<UserProfile>> = flow {
        emit(Result.Loading)
        try {
            // Check if we have a token
            val token = tokenManager.getTokenOnce()
            if (token.isNullOrEmpty()) {
                Logger.w("AuthRepository", "No token available")
                emit(Result.Error("No authentication token"))
                return@flow
            }

            Logger.d("AuthRepository", "Fetching current user")

            // Interceptor will automatically add the Bearer token
            // If token is expired (401), interceptor will attempt automatic refresh
            val user = authApi.getCurrentUser()
            Logger.i("AuthRepository", "User fetched successfully: ${user.username}")

            emit(Result.Success(user))
        } catch (e: HttpException) {
            Logger.e("AuthRepository", "HTTP ${e.code()} error fetching current user", e)
            if (e.code() == 401) {
                Logger.w("AuthRepository", "Authentication failed, clearing token")
                tokenManager.clearToken()
                emit(Result.Error("Session expired. Please login again."))
            } else {
                emit(Result.Error("Server error ${e.code()}. Please try again."))
            }
        } catch (e: Exception) {
            Logger.e("AuthRepository", "Get current user error", e)
            emit(Result.Error(e.message ?: "Failed to get user"))
        }
    }
}
