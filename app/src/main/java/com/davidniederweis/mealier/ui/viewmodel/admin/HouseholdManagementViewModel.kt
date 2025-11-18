package com.davidniederweis.mealier.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import com.davidniederweis.mealier.data.model.household.Household

sealed class HouseholdManagementState {
    object Idle : HouseholdManagementState()
    object Loading : HouseholdManagementState()
    data class Success(val households: List<Household>) : HouseholdManagementState()
    data class Error(val message: String) : HouseholdManagementState()
}

class HouseholdManagementViewModel(
) : ViewModel()
