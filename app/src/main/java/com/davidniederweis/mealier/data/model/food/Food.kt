package com.davidniederweis.mealier.data.model.food

import com.davidniederweis.mealier.data.model.label.Label
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Food(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("pluralName")
    val pluralName: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("onHand")
    val onHand: Boolean = false,

    @SerializedName("label")
    val label: Label? = null
)
