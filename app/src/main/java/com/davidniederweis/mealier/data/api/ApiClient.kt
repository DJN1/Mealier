package com.davidniederweis.mealier.data.api

import com.davidniederweis.mealier.BuildConfig
import com.davidniederweis.mealier.data.security.SecureDataStoreManager
import com.davidniederweis.mealier.data.preferences.BiometricsPreferences
import com.davidniederweis.mealier.data.preferences.ServerPreferences
import com.davidniederweis.mealier.util.Logger
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

class ApiClient(
    private val tokenManager: SecureDataStoreManager,
    private val biometricsPreferences: BiometricsPreferences,
    private val serverPreferences: ServerPreferences
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        explicitNulls = true  // Include null fields in serialization
        encodeDefaults = true  // Include default values in serialization
    }

    private val authInterceptor = Interceptor { chain ->
        val token = tokenManager.getTokenSync()
        val originalRequest = chain.request()

        Logger.d("ApiClient", "Preparing request to: ${originalRequest.url}")

        val request = if (token != null) {
            Logger.d("ApiClient", "Adding Bearer token to request")
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            Logger.w("ApiClient", "No token available for request")
            originalRequest
        }

        // Log request details
        val headers = mutableMapOf<String, String>()
        request.headers.forEach { (name, value) ->
            headers[name] = value
        }
        Logger.logRequest(
            url = request.url.toString(),
            method = request.method,
            headers = headers
        )

        val response = chain.proceed(request)

        // Log response
        Logger.logResponse(
            url = response.request.url.toString(),
            code = response.code,
            message = response.message
        )

        // Handle 401 Unauthorized - attempt token refresh
        if (response.code == 401 && !request.url.toString().contains("/api/auth/token")) {
            Logger.w("ApiClient", "Received 401 Unauthorized, attempting token refresh")
            response.close()
            
            val refreshed = runBlocking {
                try {
                    // Check if biometric is enabled and we have stored credentials
                    val biometricEnabled = biometricsPreferences.biometricEnabled.first()
                    if (biometricEnabled) {
                        val username = tokenManager.getUsername()
                        val password = tokenManager.getPassword()
                        
                        if (username != null && password != null) {
                            Logger.i("ApiClient", "Attempting automatic token refresh with stored credentials")
                            
                            // Try to get a new token
                            val tokenResponse = authApi.login(username, password)
                            tokenManager.saveToken(tokenResponse.accessToken)
                            
                            Logger.i("ApiClient", "Token refresh successful")
                            true
                        } else {
                            Logger.w("ApiClient", "Biometric enabled but credentials not found")
                            tokenManager.clearToken()
                            false
                        }
                    } else {
                        Logger.w("ApiClient", "Biometric not enabled, clearing token")
                        tokenManager.clearToken()
                        false
                    }
                } catch (e: Exception) {
                    Logger.e("ApiClient", "Token refresh failed: ${e.message}", e)
                    tokenManager.clearToken()
                    false
                }
            }
            
            // Retry the original request with new token if refresh succeeded
            if (refreshed) {
                val newToken = tokenManager.getTokenSync()
                if (newToken != null) {
                    Logger.i("ApiClient", "Retrying original request with new token")
                    val newRequest = originalRequest.newBuilder()
                        .header("Authorization", "Bearer $newToken")
                        .build()
                    return@Interceptor chain.proceed(newRequest)
                }
            }
            
            // If refresh failed, return a new 401 response
            return@Interceptor response.newBuilder()
                .code(401)
                .message("Unauthorized - Token expired")
                .build()
        }

        response
    }

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        Logger.v("OkHttp", message)
    }.apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    // Get base URL from preferences or use BuildConfig as fallback
    private val baseUrl: String by lazy {
        runBlocking {
            val url = serverPreferences.getServerUrlOnce()
            if (url.isNotBlank()) {
                Logger.i("ApiClient", "Using server URL from preferences: $url")
                url
            } else {
                Logger.i("ApiClient", "Using default server URL from BuildConfig: ${BuildConfig.BASE_URL}")
                BuildConfig.BASE_URL
            }
        }
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }

    // Create auth API first (needed for token refresh in interceptor)
    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }
    
    val recipeApi: RecipeApi by lazy {
        retrofit.create(RecipeApi::class.java)
    }
    
    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }
    
    // Expose authApi for repository use
//    fun getAuthApi(): AuthApi = authApi
}
