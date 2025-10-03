package com.davidniederweis.mealier.data.model.ingredient

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateIngredientUnitRequest(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String
)

