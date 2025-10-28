package com.davidniederweis.mealier.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import com.davidniederweis.mealier.data.model.household.Household
import com.davidniederweis.mealier.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow

sealed class HouseholdManagementState {
    object Idle : HouseholdManagementState()
    object Loading : HouseholdManagementState()
    data class Success(val households: List<Household>) : HouseholdManagementState()
    data class Error(val message: String) : HouseholdManagementState()
}

class HouseholdManagementViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _householdManagementState = MutableStateFlow<HouseholdManagementState>(HouseholdManagementState.Idle)

}
