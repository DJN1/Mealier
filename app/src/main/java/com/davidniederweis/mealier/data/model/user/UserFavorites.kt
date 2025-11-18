package com.davidniederweis.mealier.data.model.user

import kotlinx.serialization.Serializable

@Serializable
data class UserRatingOut(
    val recipeId: String,
    val rating: Double? = null,
    val isFavorite: Boolean? = null,
    val userId: String? = null,
    val id: String? = null
)

@Serializable
data class UserFavoritesResponse(
    val ratings: List<UserRatingOut> = emptyList()
)
