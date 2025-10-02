package com.davidniederweis.mealier.data.model.unit

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable


data class CreateUnitRequest(
    @SerializedName("name")
    val name: String,

    @SerializedName("pluralName")
    val pluralName: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("abbreviation")
    val abbreviation: String? = null,

    @SerializedName("pluralAbbreviation")
    val pluralAbbreviation: String? = null,

    @SerializedName("useAbbreviation")
    val useAbbreviation: Boolean = false,

    @SerializedName("fraction")
    val fraction: Boolean = true
)
