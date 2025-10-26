package com.davidniederweis.mealier.data.model.category

import kotlinx.serialization.Serializable

@Serializable
data class CategoryListResponse(
    val items: List<Category>
)
