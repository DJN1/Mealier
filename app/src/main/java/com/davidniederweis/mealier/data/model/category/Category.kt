package com.davidniederweis.mealier.data.model.category

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: String,
    val name: String,
    val slug: String
)
