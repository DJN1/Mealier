package com.davidniederweis.mealier

import android.app.Application
import com.davidniederweis.mealier.data.api.ApiClient
import com.davidniederweis.mealier.data.preferences.BiometricsPreferences
import com.davidniederweis.mealier.data.preferences.ThemePreferences
import com.davidniederweis.mealier.data.repository.AuthRepository
import com.davidniederweis.mealier.data.repository.RecipeRepository
import com.davidniederweis.mealier.data.security.SecureDataStoreManager
import com.davidniederweis.mealier.ui.viewmodel.ViewModelFactory

class MealierApplication : Application() {

    // Initialize dependencies
    private val secureDataStore by lazy { SecureDataStoreManager(this) }
    private val themePreferences by lazy { ThemePreferences(this) }
    private val biometricsPreferences by lazy { BiometricsPreferences(this) }
    private val apiClient by lazy { ApiClient(secureDataStore, biometricsPreferences) }
    private val authRepository by lazy {
        AuthRepository(apiClient.getAuthApi(), secureDataStore)
    }
    private val recipeRepository by lazy { RecipeRepository(apiClient.recipeApi) }

    // ViewModel factory
    val viewModelFactory by lazy {
        ViewModelFactory(
            authRepository = authRepository,
            userApi = apiClient.userApi,
            tokenManager = secureDataStore,
            recipeRepository = recipeRepository,
            themePreferences = themePreferences,
            biometricsPreferences = biometricsPreferences
        )
    }

    override fun onCreate() {
        super.onCreate()
    }
}
