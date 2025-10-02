package com.davidniederweis.mealier.data.model.food

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


@Serializable
data class CreateFoodRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("pluralName")
    val pluralName: String? = null,

    @SerializedName("description")
    val description: String? = null
)
