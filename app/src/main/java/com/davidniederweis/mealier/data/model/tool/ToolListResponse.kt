package com.davidniederweis.mealier.data.model.tool

import kotlinx.serialization.Serializable

@Serializable
data class ToolListResponse(
    val items: List<Tool>
)
