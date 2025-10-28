package com.davidniederweis.mealier.data.repository

import com.davidniederweis.mealier.data.api.RecipeApi
import com.davidniederweis.mealier.data.model.recipe.CreateRecipeFromUrlRequest
import com.davidniederweis.mealier.data.model.recipe.RecipeDetail

class RecipeImportRepository(private val recipeApi: RecipeApi) {
    suspend fun importRecipeFromUrl(url: String): Result<RecipeDetail> {
        return try {
            val request = CreateRecipeFromUrlRequest(url)
            val response = recipeApi.createRecipeFromUrl(request)
            Result.Success(response)
        } catch (e: Exception) {
            Result.Error(e.message ?: "An unknown error occurred while importing the recipe.", e)
        }
    }
}
