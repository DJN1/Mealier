package com.davidniederweis.mealier.data.model.food

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Food(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("pluralName")
    val pluralName: String?,

    @SerializedName("description")
    val description: String?
)
