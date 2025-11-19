package com.davidniederweis.mealier.data.model.ingredient

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CreateIngredientUnitRequest(
    @SerialName("id")
    val id: String,

    @SerialName("name")
    val name: String
)

