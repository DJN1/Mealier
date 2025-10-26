package com.davidniederweis.mealier

import android.app.Application
import com.davidniederweis.mealier.data.api.ApiClient
import com.davidniederweis.mealier.data.preferences.BiometricsPreferences
import com.davidniederweis.mealier.data.preferences.ServerPreferences
import com.davidniederweis.mealier.data.preferences.ThemePreferences
import com.davidniederweis.mealier.data.repository.AuthRepository
import com.davidniederweis.mealier.data.repository.HouseholdRepository
import com.davidniederweis.mealier.data.repository.HouseholdRepositoryImpl
import com.davidniederweis.mealier.data.repository.RecipeRepository
import com.davidniederweis.mealier.data.repository.UserRepository
import com.davidniederweis.mealier.data.repository.UserRepositoryImpl
import com.davidniederweis.mealier.data.security.SecureDataStoreManager
import com.davidniederweis.mealier.ui.viewmodel.ViewModelFactory

class MealierApplication : Application() {

    // Initialize dependencies
    private val secureDataStore by lazy { SecureDataStoreManager(this) }
    private val themePreferences by lazy { ThemePreferences(this) }
    private val biometricsPreferences by lazy { BiometricsPreferences(this) }
    private val serverPreferences by lazy { ServerPreferences(this) }
    private val apiClient by lazy { ApiClient(secureDataStore, biometricsPreferences, serverPreferences) }
    private val authRepository by lazy {
        AuthRepository(apiClient.authApi, secureDataStore)
    }
    private val recipeRepository by lazy { RecipeRepository(apiClient.recipeApi) }
    private val userRepository: UserRepository by lazy { UserRepositoryImpl(apiClient.userApi) }
    private val householdRepository: HouseholdRepository by lazy { HouseholdRepositoryImpl(apiClient.householdApi) }

    // ViewModel factory
    val viewModelFactory by lazy {
        ViewModelFactory(
            authRepository = authRepository,
            userApi = apiClient.userApi,
            tokenManager = secureDataStore,
            recipeRepository = recipeRepository,
            userRepository = userRepository,
            householdRepository = householdRepository,
            themePreferences = themePreferences,
            biometricsPreferences = biometricsPreferences,
            serverPreferences = serverPreferences
        )
    }

    override fun onCreate() {
        super.onCreate()
    }
}
