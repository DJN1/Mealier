package com.davidniederweis.mealier.data.model.unit

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CreateUnitRequest(
    @SerialName("name")
    val name: String,

    @SerialName("pluralName")
    val pluralName: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("abbreviation")
    val abbreviation: String? = null,

    @SerialName("pluralAbbreviation")
    val pluralAbbreviation: String? = null,

    @SerialName("useAbbreviation")
    val useAbbreviation: Boolean = false,

    @SerialName("fraction")
    val fraction: Boolean = true
)
