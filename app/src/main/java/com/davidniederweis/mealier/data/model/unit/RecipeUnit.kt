package com.davidniederweis.mealier.data.model.unit

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeUnit(
    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String,

    @SerializedName("pluralName")
    val pluralName: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("abbreviation")
    val abbreviation: String?,

    @SerializedName("pluralAbbreviation")
    val pluralAbbreviation: String?,

    @SerializedName("useAbbreviation")
    val useAbbreviation: Boolean?,

    @SerializedName("fraction")
    val fraction: Boolean?
)

