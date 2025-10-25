package com.davidniederweis.mealier.data.model.label

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class Label(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String
)
