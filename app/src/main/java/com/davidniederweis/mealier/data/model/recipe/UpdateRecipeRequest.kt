package com.davidniederweis.mealier.data.model.recipe

import com.davidniederweis.mealier.data.model.ingredient.CreateIngredientRequest
import com.davidniederweis.mealier.data.model.instruction.CreateInstructionRequest
import com.davidniederweis.mealier.data.model.nutrition.CreateNutritionRequest
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class UpdateRecipeRequest(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("recipeIngredient")
    val recipeIngredient: List<CreateIngredientRequest>? = null,

    @SerializedName("recipeInstructions")
    val recipeInstructions: List<CreateInstructionRequest>? = null,

    @SerializedName("recipeYield")
    val recipeYield: String? = null,

    @SerializedName("recipeServings")
    val recipeServings: Double? = null,

    @SerializedName("prepTime")
    val prepTime: String? = null,

    @SerializedName("cookTime")
    val cookTime: String? = null,

    @SerializedName("totalTime")
    val totalTime: String? = null,

    @SerializedName("nutrition")
    val nutrition: CreateNutritionRequest? = null
)
