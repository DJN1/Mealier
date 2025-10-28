package com.davidniederweis.mealier.data.model

import kotlinx.serialization.Serializable

@Serializable
data class CreateCookBook(
    val name: String,
    val description: String,
    val public: Boolean,
    val queryFilterString: String
)