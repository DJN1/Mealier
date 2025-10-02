package com.davidniederweis.mealier.data.model.recipe

import com.davidniederweis.mealier.data.model.ingredient.RecipeIngredient
import com.davidniederweis.mealier.data.model.nutrition.Nutrition
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetail(
    val id: String,
    val name: String?,
    val slug: String,
    val image: String? = null,
    val description: String? = null,
    val recipeIngredient: List<RecipeIngredient> = emptyList(),
    val recipeInstructions: List<RecipeInstruction>? = null,
    val rating: Double? = null,
    val prepTime: String? = null,
    val cookTime: String? = null,
    val performTime: String? = null,
    val totalTime: String? = null,
    val recipeServings: Double,
    val recipeYield: String? = null,
    val recipeYieldQuantity: Double? = null,
    val recipeCategory: List<RecipeCategory>? = null,
    val notes: List<RecipeNote>? = null,
    val tags: List<RecipeTag>? = null,
    val nutrition: Nutrition? = null,
    val orgURL: String? = null
)