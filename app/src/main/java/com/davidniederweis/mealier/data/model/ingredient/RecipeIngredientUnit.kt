package com.davidniederweis.mealier.data.model.ingredient

import kotlinx.serialization.Serializable

@Serializable
data class RecipeIngredientUnit(
    val id: String? = null,
    val name: String,
    val pluralName: String? = null,
    val description: String? = null,
    val extras: Map<String, String>? = null,
    val fraction: Boolean = false,
    val abbreviation: String? = null,
    val pluralAbbreviation: String? = null,
    val useAbbreviation: Boolean = false,
    val aliases: List<String>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)