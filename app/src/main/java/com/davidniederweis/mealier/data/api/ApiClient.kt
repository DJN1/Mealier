package com.davidniederweis.mealier.data.api

import com.davidniederweis.mealier.BuildConfig
import com.davidniederweis.mealier.data.preferences.ServerPreferences
import com.davidniederweis.mealier.data.security.SecureDataStoreManager
import com.davidniederweis.mealier.util.Logger
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ApiClient(
    tokenManager: SecureDataStoreManager,
    serverPreferences: ServerPreferences,
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        explicitNulls = true  // Include null fields in serialization
        encodeDefaults = true  // Include default values in serialization
    }

    private val authInterceptor = Interceptor { chain ->
        val token = runBlocking { tokenManager.getTokenSync() }
        val originalRequest = chain.request()

        val request = if (token != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
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

    private val okHttpClient: OkHttpClient

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

    private val retrofit: Retrofit

    val authApi: AuthApi
    val recipeApi: RecipeApi
    val userApi: UserApi
    val householdApi: HouseholdApi

    init {
        // A separate client for the authenticator to avoid circular dependency
        val authClientForAuthenticator = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val authRetrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(authClientForAuthenticator)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        val authApiForAuthenticator = authRetrofit.create(AuthApi::class.java)

        val tokenAuthenticator = TokenAuthenticator(tokenManager, authApiForAuthenticator)

        okHttpClient = OkHttpClient.Builder()
            .authenticator(tokenAuthenticator)
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        authApi = retrofit.create(AuthApi::class.java)
        recipeApi = retrofit.create(RecipeApi::class.java)
        userApi = retrofit.create(UserApi::class.java)
        householdApi = retrofit.create(HouseholdApi::class.java)
    }
}