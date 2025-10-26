package com.davidniederweis.mealier.data.model.cookbook

import kotlinx.serialization.Serializable

@Serializable
data class CookbookListResponse(
    val items: List<Cookbook>
)
