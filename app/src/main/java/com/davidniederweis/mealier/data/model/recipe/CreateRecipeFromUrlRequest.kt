package com.davidniederweis.mealier.data.model.recipe

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateRecipeFromUrlRequest(
    @SerializedName("url")
    val url: String,

    @SerializedName("includeTags")
    val includeTags: Boolean = true
)
