package com.davidniederweis.mealier.data.model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RecipeCategory(
    val id: String? = null,
    val name: String,
    val slug: String
)