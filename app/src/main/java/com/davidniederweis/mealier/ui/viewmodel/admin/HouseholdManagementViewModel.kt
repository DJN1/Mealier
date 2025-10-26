package com.davidniederweis.mealier.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.household.Household
import com.davidniederweis.mealier.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

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
    val householdManagementState: StateFlow<HouseholdManagementState> = _householdManagementState.asStateFlow()

    fun getHouseholds() {
        viewModelScope.launch {
            _householdManagementState.value = HouseholdManagementState.Loading
            try {
                val households = recipeRepository.getHouseholds()
                _householdManagementState.value = HouseholdManagementState.Success(households)
            } catch (e: Exception) {
                _householdManagementState.value = HouseholdManagementState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}
