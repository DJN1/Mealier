package com.davidniederweis.mealier.data.model.recipe

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RecipeListResponse(
    val items: List<RecipeSummary>,
    val page: Int,
    @SerialName("per_page") val perPage: Int,
    val total: Int,
    @SerialName("total_pages") val totalPages: Int
)