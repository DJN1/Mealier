package com.davidniederweis.mealier.data.model.ingredient

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateIngredientFoodRequest(
    @SerializedName("id")
    val id: String
)
