package com.davidniederweis.mealier.data.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T>(
    val page: Int,
    val perPage: Int,
    val total: Int,
    val totalPages: Int,
    val items: List<T>
)
