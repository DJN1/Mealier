package com.davidniederweis.mealier.data.model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RecipeTag(
    val id: String? = null,
    val name: String,
    val slug: String
)