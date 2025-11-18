package com.davidniederweis.mealier.ui.viewmodel.recipe

import com.davidniederweis.mealier.data.model.recipe.RecipeSummary

sealed class FavoriteRecipesState {
    data object Idle : FavoriteRecipesState()
    data object Loading : FavoriteRecipesState()
    data class Success(val recipes: List<RecipeSummary>) : FavoriteRecipesState()
    data class Error(val message: String) : FavoriteRecipesState()
}
