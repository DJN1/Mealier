package com.davidniederweis.mealier.data.model.tag

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CreateTagRequest(
    @SerialName("name")
    val name: String
)
