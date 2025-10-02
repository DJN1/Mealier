package com.davidniederweis.mealier.data.model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RecipeSummary(
    val id: String,
    val name: String?,
    val slug: String,
    val image: String? = null,
    val description: String? = null,
    val rating: Double? = null,
    val prepTime: String? = null,
    val totalTime: String? = null,
    val recipeYield: String? = null,
    val dateAdded: String? = null,
    val dateUpdated: String? = null,
    val recipeCategory: List<RecipeCategory>? = null,
    val tags: List<RecipeTag>? = null
)