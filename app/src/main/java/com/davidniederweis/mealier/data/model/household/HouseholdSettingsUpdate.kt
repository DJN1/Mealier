package com.davidniederweis.mealier.data.model.household

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HouseholdSettingsUpdate(
    @SerialName("privateHousehold")
    val privateHousehold: Boolean,
    @SerialName("lockRecipeEditsFromOtherHouseholds")
    val lockRecipeEditsFromOtherHouseholds: Boolean,
    @SerialName("firstDayOfWeek")
    val firstDayOfWeek: Int,
    @SerialName("recipePublic")
    val recipePublic: Boolean,
    @SerialName("recipeShowNutrition")
    val recipeShowNutrition: Boolean,
    @SerialName("recipeShowAssets")
    val recipeShowAssets: Boolean,
    @SerialName("recipeLandscapeView")
    val recipeLandscapeView: Boolean,
    @SerialName("recipeDisableComments")
    val recipeDisableComments: Boolean
)
