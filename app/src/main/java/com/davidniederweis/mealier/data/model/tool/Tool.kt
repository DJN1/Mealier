package com.davidniederweis.mealier.data.model.tool

import kotlinx.serialization.Serializable

@Serializable
data class Tool(
    val id: String,
    val name: String,
    val slug: String
)
