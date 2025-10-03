package com.davidniederweis.mealier.data.model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class RecipeSettings(
    val public: Boolean = true,
    val showNutrition: Boolean = true,
    val showAssets: Boolean = false,
    val landscapeView: Boolean = false,
    val disableComments: Boolean = false,
    val locked: Boolean = false
)
