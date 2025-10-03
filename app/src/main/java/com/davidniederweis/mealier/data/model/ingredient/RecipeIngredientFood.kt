package com.davidniederweis.mealier.data.model.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class RecipeIngredientFood(
    val id: String? = null,
    val name: String,
    val pluralName: String? = null,
    val description: String? = null,
    val extras: Map<String, String>? = null,
    val labelId: String? = null,
    val aliases: List<Map<String, String>>? = null,
    val householdsWithIngredientFood: List<String>? = null,
    val label: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)