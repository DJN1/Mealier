package com.davidniederweis.mealier.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.food.CreateFoodRequest
import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class FoodManagementState {
    object Idle : FoodManagementState()
    object Loading : FoodManagementState()
    data class Success(val foods: List<Food>) : FoodManagementState()
    data class Error(val message: String) : FoodManagementState()
}

class FoodManagementViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _foodManagementState = MutableStateFlow<FoodManagementState>(FoodManagementState.Idle)
    val foodManagementState: StateFlow<FoodManagementState> = _foodManagementState.asStateFlow()

    private val _foods = MutableStateFlow<List<Food>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortAscending = MutableStateFlow(true)

    fun getFoods() {
        viewModelScope.launch {
            _foodManagementState.value = FoodManagementState.Loading
            try {
                val foods = recipeRepository.getFoods()
                _foods.value = foods
                updateFoodList()
            } catch (e: Exception) {
                _foodManagementState.value = FoodManagementState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        updateFoodList()
    }

    fun toggleSort() {
        _sortAscending.value = !_sortAscending.value
        updateFoodList()
    }

    private fun updateFoodList() {
        val filteredFoods = if (_searchQuery.value.isBlank()) {
            _foods.value
        } else {
            _foods.value.filter { it.name.contains(_searchQuery.value, ignoreCase = true) }
        }

        val sortedFoods = if (_sortAscending.value) {
            filteredFoods.sortedBy { it.name }
        } else {
            filteredFoods.sortedByDescending { it.name }
        }

        _foodManagementState.value = FoodManagementState.Success(sortedFoods)
    }

    fun createFood(food: Food) {
        viewModelScope.launch {
            try {
                val request = CreateFoodRequest(name = food.name)
                recipeRepository.createFood(request)
                getFoods() // Refresh list
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    fun updateFood() {
        viewModelScope.launch {
            try {
                // recipeRepository.updateFood(food) // There is no update food method in the api
                getFoods() // Refresh list
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    fun deleteFood(id: String) {
        viewModelScope.launch {
            try {
                recipeRepository.deleteFood(id)
                getFoods() // Refresh list
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }
}