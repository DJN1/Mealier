package com.davidniederweis.mealier.data.model.recipe

import com.davidniederweis.mealier.data.model.ingredient.RecipeIngredient
import com.davidniederweis.mealier.data.model.nutrition.Nutrition
import kotlinx.serialization.Serializable

@Serializable
data class RecipeDetail(
    val id: String,
    val userId: String? = null,
    val householdId: String? = null,
    val groupId: String? = null,
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
    val tools: List<String>? = null,
    val nutrition: Nutrition? = null,
    val orgURL: String? = null,
    val dateAdded: String? = null,
    val dateUpdated: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val lastMade: String? = null,
    val settings: RecipeSettings? = null,
    val assets: List<String>? = null,
    val extras: Map<String, String>? = null,
    val comments: List<String>? = null
)