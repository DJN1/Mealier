package com.davidniederweis.mealier.data.model.food

import androidx.annotation.Keep
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class CreateFoodRequest(
    @SerialName("name")
    val name: String
)
