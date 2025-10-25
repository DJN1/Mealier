package com.davidniederweis.mealier.data.model.food

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateFoodRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("pluralName")
    val pluralName: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("labelId")
    val labelId: String?,

    @SerializedName("onHand")
    val onHand: Boolean
)
