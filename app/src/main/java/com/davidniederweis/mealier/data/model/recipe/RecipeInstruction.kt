package com.davidniederweis.mealier.data.model.recipe

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeInstruction(
    @SerializedName("id")
    val id: String?,

    @SerializedName("title")
    val title: String?,

    @SerializedName("text")
    val text: String?,

    @SerializedName("summary")
    val summary: String?,

    @SerializedName("ingredientReferences")
    val ingredientReferences: List<String>?
)
