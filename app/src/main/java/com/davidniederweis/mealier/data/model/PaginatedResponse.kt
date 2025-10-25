package com.davidniederweis.mealier.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val page: Int,
    val per_page: Int,
    val total: Int,
    val total_pages: Int,
    val items: List<T>
)
