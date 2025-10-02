package com.davidniederweis.mealier.data.api

import com.davidniederweis.mealier.data.model.auth.TokenResponse
import com.davidniederweis.mealier.data.model.user.UserProfile
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @FormUrlEncoded
    @POST("/api/auth/token")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String
    ): TokenResponse

    @GET("/api/users/self")
    suspend fun getCurrentUser(): UserProfile  // Removed Authorization header parameter
}