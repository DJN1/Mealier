package com.davidniederweis.mealier.ui.viewmodel.recipe

import com.davidniederweis.mealier.data.model.recipe.RecipeSummary

sealed class RecipeListState {
    data object Idle : RecipeListState()
    data object Loading : RecipeListState()
    data class Success(
        val recipes: List<RecipeSummary>,
        val hasMore: Boolean
    ) : RecipeListState()
    data class Error(val message: String) : RecipeListState()
}
