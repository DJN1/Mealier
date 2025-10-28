package com.davidniederweis.mealier.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.repository.HouseholdRepository
import com.davidniederweis.mealier.util.Logger
import kotlinx.coroutines.launch

class HouseholdSettingsViewModel(private val householdRepository: HouseholdRepository) : ViewModel() {

    fun updateSettings(
        privateHousehold: Boolean,
        lockRecipeEdits: Boolean,
        allowUsersOutsideGroup: Boolean,
        showNutritionInfo: Boolean,
        showRecipeAssets: Boolean,
        defaultToLandscape: Boolean,
        disableCommenting: Boolean,
        firstDayOfWeek: String
    ) {
        viewModelScope.launch {
            try {
                Logger.d("HouseholdSettings", "Sending update request...")
                householdRepository.updateHouseholdSettings(
                    privateHousehold = privateHousehold,
                    lockRecipeEditsFromOtherHouseholds = lockRecipeEdits,
                    recipePublic = allowUsersOutsideGroup,
                    recipeShowNutrition = showNutritionInfo,
                    recipeShowAssets = showRecipeAssets,
                    recipeLandscapeView = defaultToLandscape,
                    recipeDisableComments = disableCommenting,
                    firstDayOfWeek = getDayOfWeekInt(firstDayOfWeek)
                )
                Logger.d("HouseholdSettings", "Update request successful")
            } catch (e: Exception) {
                Logger.e("HouseholdSettings", "Failed to update settings", e)
            }
        }
    }

    private fun getDayOfWeekInt(day: String): Int {
        return when (day) {
            "Sunday" -> 0
            "Monday" -> 1
            "Tuesday" -> 2
            "Wednesday" -> 3
            "Thursday" -> 4
            "Friday" -> 5
            "Saturday" -> 6
            else -> 0
        }
    }
}
