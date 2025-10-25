package com.davidniederweis.mealier.data.repository

import com.davidniederweis.mealier.data.api.RecipeApi
import com.davidniederweis.mealier.data.model.food.Food

class FoodRepository(
    private val recipeApi: RecipeApi
) {
    suspend fun getFoods(): List<Food> {
        return recipeApi.getFoods().items
    }
}