package com.davidniederweis.mealier.data.model.parser

import kotlinx.serialization.Serializable

@Serializable
data class IngredientsRequest(
    val ingredients: List<String>,
    val parser: String = "nlp"
)
