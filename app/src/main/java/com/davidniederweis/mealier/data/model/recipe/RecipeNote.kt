package com.davidniederweis.mealier.data.model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RecipeNote(
    val title: String,
    val text: String,
)
