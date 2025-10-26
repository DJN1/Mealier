package com.davidniederweis.mealier.data.api

import com.davidniederweis.mealier.data.model.household.Household
import com.davidniederweis.mealier.data.model.household.HouseholdSettingsUpdate
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface HouseholdApi {
    @PUT("/api/households/preferences")
    suspend fun updateHouseholdSettings(@Body settings: HouseholdSettingsUpdate)
}
