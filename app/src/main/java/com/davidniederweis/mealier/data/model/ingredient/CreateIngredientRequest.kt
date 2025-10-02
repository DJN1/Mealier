package com.davidniederweis.mealier.data.model.ingredient

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class CreateIngredientRequest(
    @SerializedName("quantity")
    val quantity: Double? = null,

    @SerializedName("unit")
    val unit: CreateIngredientUnitRequest? = null,

    @SerializedName("food")
    val food: CreateIngredientFoodRequest,

    @SerializedName("note")
    val note: String? = null
)
