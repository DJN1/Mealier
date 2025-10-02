package com.davidniederweis.mealier.data.model.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class RecipeIngredientFood(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val labelId: String? = null
)