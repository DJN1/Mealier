package com.davidniederweis.mealier.data.model.cookbook

import kotlinx.serialization.Serializable

@Serializable
data class Cookbook(
    val id: String,
    val name: String,
    val description: String,
    val public: Boolean,
    val queryFilterString: String,
    val slug: String,
    val position: Int,
    val groupId: String,
    val householdId: String,
    val household: CookbookHousehold
)
