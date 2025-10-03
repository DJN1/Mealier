package com.davidniederweis.mealier.data.model.recipe

import kotlinx.serialization.Serializable

@Serializable
data class UploadImageFromUrlRequest(
    val url: String
)
