package com.davidniederweis.mealier.data.model.cookbook

import kotlinx.serialization.Serializable

@Serializable
data class UpdateCookbook(
    val name: String,
    val description: String,
    val slug: String,
    val position: Int,
    val public: Boolean,
    val queryFilterString: String,
    val groupId: String,
    val householdId: String,
    val id: String,
    val queryFilter: QueryFilter,
    val household: CookbookHousehold
)

@Serializable
data class CookbookHousehold(
    val id: String,
    val name: String
)
