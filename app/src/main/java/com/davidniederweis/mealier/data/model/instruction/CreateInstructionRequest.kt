package com.davidniederweis.mealier.data.model.instruction

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateInstructionRequest(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("text")
    val text: String? = null,

    @SerializedName("summary")
    val summary: String? = null
)
