package com.davidniederweis.mealier.data.model.ingredient

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Keep
@Serializable
data class CreateIngredientRequest(
    @SerialName("quantity")
    val quantity: Double? = null,

    @SerialName("unit")
    val unit: CreateIngredientUnitRequest? = null,

    @SerialName("food")
    val food: CreateIngredientFoodRequest,

    @SerialName("note")
    val note: String? = null,

    @SerialName("originalText")
    val originalText: String? = null
)
