package com.davidniederweis.mealier.data.model.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class RecipeIngredientUnit(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val fraction: Boolean = false,
    val abbreviation: String? = null,
    val useAbbreviation: Boolean = false
)