package com.davidniederweis.mealier.data.api

import com.davidniederweis.mealier.data.model.user.UserProfile
import retrofit2.http.GET

interface UserApi {
    @GET("/api/users/self")
    suspend fun getCurrentUser(): UserProfile
}