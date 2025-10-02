package com.davidniederweis.mealier.data.api

import com.davidniederweis.mealier.BuildConfig
import com.davidniederweis.mealier.data.security.SecureDataStoreManager
import com.davidniederweis.mealier.util.Logger
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class ApiClient(
    private val tokenManager: SecureDataStoreManager
) {
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
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

    private val retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
    val recipeApi: RecipeApi = retrofit.create(RecipeApi::class.java)
    val userApi: UserApi = retrofit.create(UserApi::class.java)

    init {
        Logger.i("ApiClient", "Initialized with base URL: ${BuildConfig.BASE_URL}")
    }
}
