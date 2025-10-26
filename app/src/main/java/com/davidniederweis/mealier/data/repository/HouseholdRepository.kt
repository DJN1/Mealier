package com.davidniederweis.mealier.data.repository

import com.davidniederweis.mealier.data.api.HouseholdApi
import com.davidniederweis.mealier.data.model.household.Household
import com.davidniederweis.mealier.data.model.household.HouseholdSettingsUpdate
import com.davidniederweis.mealier.util.Logger

interface HouseholdRepository {
    suspend fun updateHouseholdSettings(
        privateHousehold: Boolean,
        lockRecipeEditsFromOtherHouseholds: Boolean,
        recipePublic: Boolean,
        recipeShowNutrition: Boolean,
        recipeShowAssets: Boolean,
        recipeLandscapeView: Boolean,
        recipeDisableComments: Boolean,
        firstDayOfWeek: Int
    )
}

class HouseholdRepositoryImpl(
    private val householdApi: HouseholdApi
) : HouseholdRepository {

    override suspend fun updateHouseholdSettings(
        privateHousehold: Boolean,
        lockRecipeEditsFromOtherHouseholds: Boolean,
        recipePublic: Boolean,
        recipeShowNutrition: Boolean,
        recipeShowAssets: Boolean,
        recipeLandscapeView: Boolean,
        recipeDisableComments: Boolean,
        firstDayOfWeek: Int
    ) {
        try {
            val settings = HouseholdSettingsUpdate(
                privateHousehold = privateHousehold,
                lockRecipeEditsFromOtherHouseholds = lockRecipeEditsFromOtherHouseholds,
                recipePublic = recipePublic,
                recipeShowNutrition = recipeShowNutrition,
                recipeShowAssets = recipeShowAssets,
                recipeLandscapeView = recipeLandscapeView,
                recipeDisableComments = recipeDisableComments,
                firstDayOfWeek = firstDayOfWeek
            )
            householdApi.updateHouseholdSettings(settings)
        } catch (e: Exception) {
            Logger.e("HouseholdRepository", "Error updating household settings: ${e.message}", e)
        }
    }
}
