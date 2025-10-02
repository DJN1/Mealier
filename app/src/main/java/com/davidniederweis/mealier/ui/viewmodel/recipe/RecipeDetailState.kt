package com.davidniederweis.mealier.ui.viewmodel.recipe

import com.davidniederweis.mealier.data.model.recipe.RecipeDetail

sealed class RecipeDetailState {
    data object Idle : RecipeDetailState()
    data object Loading : RecipeDetailState()
    data class Success(val recipe: RecipeDetail) : RecipeDetailState()
    data class Error(val message: String) : RecipeDetailState()
}
