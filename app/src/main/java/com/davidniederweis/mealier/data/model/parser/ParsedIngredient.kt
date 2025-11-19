package com.davidniederweis.mealier.data.model.parser

import com.davidniederweis.mealier.data.model.ingredient.RecipeIngredient
import kotlinx.serialization.Serializable

@Serializable
data class ParsedIngredient(
    val input: String? = null,
    val ingredient: RecipeIngredient
)
