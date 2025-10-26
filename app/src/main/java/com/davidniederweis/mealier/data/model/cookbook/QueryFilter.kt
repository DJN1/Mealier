package com.davidniederweis.mealier.data.model.cookbook

import kotlinx.serialization.Serializable

@Serializable
data class QueryFilter(
    val parts: List<QueryFilterPart> = emptyList()
)

@Serializable
data class QueryFilterPart(
    val leftParenthesis: String? = null,
    val rightParenthesis: String? = null,
    val logicalOperator: String? = null,
    val attributeName: String? = null,
    val relationalOperator: String? = null,
    val value: List<String>? = null
)
