package com.davidniederweis.mealier.data.model.nutrition

import kotlinx.serialization.Serializable

@Serializable
data class Nutrition(
    val calories: String? = null,
    val proteinContent: String? = null,
    val carbohydrateContent: String? = null,
    val fatContent: String? = null
)