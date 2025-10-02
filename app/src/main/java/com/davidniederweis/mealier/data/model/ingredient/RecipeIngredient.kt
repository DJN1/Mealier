package com.davidniederweis.mealier.data.model.ingredient

import com.davidniederweis.mealier.data.model.ingredient.RecipeIngredientFood
import com.davidniederweis.mealier.data.model.ingredient.RecipeIngredientUnit
import kotlinx.serialization.Serializable

@Serializable
data class RecipeIngredient(
    val title: String? = null,
    val note: String? = null,
    val unit: RecipeIngredientUnit? = null,
    val food: RecipeIngredientFood? = null,
    val disableAmount: Boolean = false,
    val quantity: Double = 1.0,
    val originalText: String? = null,
    val referenceId: String? = null
)