package com.davidniederweis.mealier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davidniederweis.mealier.data.api.UserApi
import com.davidniederweis.mealier.data.preferences.BiometricsPreferences
import com.davidniederweis.mealier.data.preferences.ServerPreferences
import com.davidniederweis.mealier.data.preferences.ThemePreferences
import com.davidniederweis.mealier.data.repository.AuthRepository
import com.davidniederweis.mealier.data.repository.RecipeRepository
import com.davidniederweis.mealier.data.security.SecureDataStoreManager
import com.davidniederweis.mealier.ui.viewmodel.auth.AuthViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.BiometricsViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.ServerViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.ThemeViewModel
import com.davidniederweis.mealier.ui.viewmodel.profile.ProfileViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.AddRecipeViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.EditRecipeViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val userApi: UserApi,
    private val tokenManager: SecureDataStoreManager,
    private val recipeRepository: RecipeRepository,
    private val themePreferences: ThemePreferences,
    private val biometricsPreferences: BiometricsPreferences,
    private val serverPreferences: ServerPreferences,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository, tokenManager, biometricsPreferences) as T
            }

            modelClass.isAssignableFrom(RecipeViewModel::class.java) -> {
                RecipeViewModel(recipeRepository, serverPreferences) as T
            }

            modelClass.isAssignableFrom(AddRecipeViewModel::class.java) -> {
                AddRecipeViewModel(recipeRepository) as T
            }

            modelClass.isAssignableFrom(EditRecipeViewModel::class.java) -> {
                EditRecipeViewModel(recipeRepository) as T
            }

            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userApi) as T
            }

            modelClass.isAssignableFrom(ThemeViewModel::class.java) -> {
                ThemeViewModel(themePreferences) as T
            }

            modelClass.isAssignableFrom(BiometricsViewModel::class.java) -> {
                BiometricsViewModel(biometricsPreferences) as T
            }

            modelClass.isAssignableFrom(ServerViewModel::class.java) -> {
                ServerViewModel(serverPreferences) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
