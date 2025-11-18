package com.davidniederweis.mealier.data.api

import com.davidniederweis.mealier.data.model.PaginatedResponse
import com.davidniederweis.mealier.data.model.user.CreateInvitationRequest
import com.davidniederweis.mealier.data.model.user.InvitationResponse
import com.davidniederweis.mealier.data.model.user.SendInvitationEmailRequest
import com.davidniederweis.mealier.data.model.user.User
import com.davidniederweis.mealier.data.model.user.UserProfile
import com.davidniederweis.mealier.data.model.user.UserFavoritesResponse
import com.davidniederweis.mealier.data.model.user.UserRatingSummary
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {
    @GET("/api/users/self")
    suspend fun getCurrentUser(): UserProfile

    @GET("/api/users/self/ratings/{recipe_id}")
    suspend fun getCurrentUserRatingForRecipe(
        @Path("recipe_id") recipeId: String
    ): UserRatingSummary

    @POST("/api/users/{id}/favorites/{slug}")
    suspend fun addFavorite(
        @Path("id") userId: String,
        @Path("slug") recipeSlug: String
    )

    @DELETE("/api/users/{id}/favorites/{slug}")
    suspend fun removeFavorite(
        @Path("id") userId: String,
        @Path("slug") recipeSlug: String
    )

    @GET("/api/admin/users")
    suspend fun getAllUsers(): PaginatedResponse<User>

    @POST("/api/households/invitations")
    suspend fun createInvitation(@Body request: CreateInvitationRequest): InvitationResponse

    @POST("/api/households/invitations/email")
    suspend fun sendInvitationEmail(@Body request: SendInvitationEmailRequest)

    @DELETE("/api/admin/users/{id}")
    suspend fun deleteUser(@Path("id") userId: String)

    @PUT("/api/admin/users/{id}")
    suspend fun updateUser(@Path("id") userId: String, @Body user: User)

    @GET("/api/users/{id}/favorites")
    suspend fun getFavorites(@Path("id") userId: String): UserFavoritesResponse
}
