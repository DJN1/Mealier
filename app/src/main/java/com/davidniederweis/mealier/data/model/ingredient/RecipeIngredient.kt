package com.davidniederweis.mealier.data.model.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class RecipeIngredient(
    val title: String? = null,
    val note: String? = null,
    val unit: RecipeIngredientUnit? = null,
    val food: RecipeIngredientFood? = null,
    val disableAmount: Boolean = false,
    val quantity: Double = 1.0,
    val display: String? = null,
    val originalText: String? = null,
    val referenceId: String? = null
)