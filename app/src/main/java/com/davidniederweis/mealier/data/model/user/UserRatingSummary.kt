package com.davidniederweis.mealier.data.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRatingSummary(
    val recipeId: String,
    val rating: Double? = null,
    val isFavorite: Boolean? = null
)
