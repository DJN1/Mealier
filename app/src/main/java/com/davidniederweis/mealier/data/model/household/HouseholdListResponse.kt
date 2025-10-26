package com.davidniederweis.mealier.data.model.household

import kotlinx.serialization.Serializable

@Serializable
data class HouseholdListResponse(
    val items: List<Household>
)
