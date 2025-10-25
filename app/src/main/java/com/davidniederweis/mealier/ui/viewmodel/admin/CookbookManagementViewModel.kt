package com.davidniederweis.mealier.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.recipe.RecipeSummary
import com.davidniederweis.mealier.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class CookbookManagementState {
    object Idle : CookbookManagementState()
    object Loading : CookbookManagementState()
    data class Success(val recipes: List<RecipeSummary>) : CookbookManagementState()
    data class Error(val message: String) : CookbookManagementState()
}

class CookbookManagementViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _cookbookManagementState = MutableStateFlow<CookbookManagementState>(CookbookManagementState.Idle)
    val cookbookManagementState: StateFlow<CookbookManagementState> = _cookbookManagementState.asStateFlow()

    private val _recipes = MutableStateFlow<List<RecipeSummary>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortAscending = MutableStateFlow(true)
    val sortAscending: StateFlow<Boolean> = _sortAscending.asStateFlow()

    fun getRecipes() {
        viewModelScope.launch {
            _cookbookManagementState.value = CookbookManagementState.Loading
            try {
                val recipes = recipeRepository.getAllRecipes()
                _recipes.value = recipes
                updateRecipeList()
            } catch (e: Exception) {
                _cookbookManagementState.value = CookbookManagementState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        updateRecipeList()
    }

    fun toggleSort() {
        _sortAscending.value = !_sortAscending.value
        updateRecipeList()
    }

    private fun updateRecipeList() {
        val filteredRecipes = if (_searchQuery.value.isBlank()) {
            _recipes.value
        } else {
            _recipes.value.filter { it.name?.contains(_searchQuery.value, ignoreCase = true) ?: false }
        }

        val sortedRecipes = if (_sortAscending.value) {
            filteredRecipes.sortedBy { it.name ?: "" }
        } else {
            filteredRecipes.sortedByDescending { it.name ?: "" }
        }

        _cookbookManagementState.value = CookbookManagementState.Success(sortedRecipes)
    }

    fun deleteRecipe(slug: String) {
        viewModelScope.launch {
            try {
                recipeRepository.deleteRecipe(slug)
                getRecipes() // Refresh list
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }
}
