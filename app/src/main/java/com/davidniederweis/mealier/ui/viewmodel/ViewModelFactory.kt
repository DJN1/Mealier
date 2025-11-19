package com.davidniederweis.mealier.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.davidniederweis.mealier.data.api.UserApi
import com.davidniederweis.mealier.data.preferences.AppPreferences
import com.davidniederweis.mealier.data.preferences.BiometricsPreferences
import com.davidniederweis.mealier.data.preferences.ServerPreferences
import com.davidniederweis.mealier.data.preferences.ThemePreferences
import com.davidniederweis.mealier.data.repository.AuthRepository
import com.davidniederweis.mealier.data.repository.HouseholdRepository
import com.davidniederweis.mealier.data.repository.ImportRecipeFromUrlUseCase
import com.davidniederweis.mealier.data.repository.RecipeRepository
import com.davidniederweis.mealier.data.repository.UserRepository
import com.davidniederweis.mealier.ui.viewmodel.admin.CookbookManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.admin.CreateCookbookViewModel
import com.davidniederweis.mealier.ui.viewmodel.admin.FoodManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.admin.HouseholdManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.admin.HouseholdSettingsViewModel
import com.davidniederweis.mealier.ui.viewmodel.admin.TagManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.admin.UnitManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.admin.UserManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.auth.AuthViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.BiometricsViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.ServerViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.SettingsViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.ThemeViewModel
import com.davidniederweis.mealier.ui.viewmodel.profile.ProfileViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.AddRecipeViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.CookbookViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.EditRecipeViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipeimport.RecipeImportViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val userApi: UserApi,
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository,
    private val householdRepository: HouseholdRepository,
    private val themePreferences: ThemePreferences,
    private val biometricsPreferences: BiometricsPreferences,
    private val serverPreferences: ServerPreferences,
    private val appPreferences: AppPreferences,
    private val importRecipeFromUrlUseCase: ImportRecipeFromUrlUseCase
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(authRepository, biometricsPreferences) as T
            }

            modelClass.isAssignableFrom(RecipeViewModel::class.java) -> {
                RecipeViewModel(recipeRepository, serverPreferences, userApi) as T
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

            modelClass.isAssignableFrom(SettingsViewModel::class.java) -> {
                SettingsViewModel(appPreferences) as T
            }

            modelClass.isAssignableFrom(FoodManagementViewModel::class.java) -> {
                FoodManagementViewModel(recipeRepository) as T
            }

            modelClass.isAssignableFrom(UnitManagementViewModel::class.java) -> {
                UnitManagementViewModel(recipeRepository) as T
            }

            modelClass.isAssignableFrom(TagManagementViewModel::class.java) -> {
                TagManagementViewModel(recipeRepository) as T
            }

            modelClass.isAssignableFrom(CookbookManagementViewModel::class.java) -> {
                CookbookManagementViewModel(recipeRepository) as T
            }

            modelClass.isAssignableFrom(UserManagementViewModel::class.java) -> {
                UserManagementViewModel(userRepository) as T
            }

            modelClass.isAssignableFrom(HouseholdManagementViewModel::class.java) -> {
                HouseholdManagementViewModel() as T
            }

            modelClass.isAssignableFrom(HouseholdSettingsViewModel::class.java) -> {
                HouseholdSettingsViewModel(householdRepository) as T
            }

            modelClass.isAssignableFrom(CreateCookbookViewModel::class.java) -> {
                CreateCookbookViewModel(recipeRepository) as T
            }

            modelClass.isAssignableFrom(EditRecipeViewModel::class.java) -> {
                EditRecipeViewModel(recipeRepository) as T
            }

            modelClass.isAssignableFrom(CookbookViewModel::class.java) -> {
                CookbookViewModel(recipeRepository) as T
            }

            modelClass.isAssignableFrom(RecipeImportViewModel::class.java) -> {
                RecipeImportViewModel(importRecipeFromUrlUseCase) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
