package com.davidniederweis.mealier.data.api

import com.davidniederweis.mealier.data.model.food.CreateFoodRequest
import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.model.food.FoodListResponse
import com.davidniederweis.mealier.data.model.recipe.*
import com.davidniederweis.mealier.data.model.tag.CreateTagRequest
import com.davidniederweis.mealier.data.model.tag.Tag
import com.davidniederweis.mealier.data.model.tag.TagListResponse
import com.davidniederweis.mealier.data.model.unit.CreateUnitRequest
import com.davidniederweis.mealier.data.model.unit.RecipeUnit
import com.davidniederweis.mealier.data.model.unit.UnitListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface RecipeApi {
    @GET("api/recipes")
    suspend fun getRecipes(
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 50,
        @Query("search") search: String? = null
    ): RecipeListResponse

    @GET("api/recipes/{slug}")
    suspend fun getRecipeDetail(@Path("slug") slug: String): RecipeDetail

    // Create recipe manually
    @POST("api/recipes")
    suspend fun createRecipe(@Body request: CreateRecipeRequest): RecipeDetail

    // Create recipe from URL
    @POST("api/recipes/create/url")
    suspend fun createRecipeFromUrl(@Body request: CreateRecipeFromUrlRequest): RecipeDetail

    // Update recipe
    @PUT("api/recipes/{slug}")
    suspend fun updateRecipe(
        @Path("slug") slug: String,
        @Body recipe: RecipeDetail
    ): RecipeDetail

    // Delete recipe
    @DELETE("api/recipes/{slug}")
    suspend fun deleteRecipe(@Path("slug") slug: String)

    // Upload recipe image from file
    @Multipart
    @PUT("api/recipes/{slug}/image")
    suspend fun uploadRecipeImage(
        @Path("slug") slug: String,
        @Part image: MultipartBody.Part,
        @Part("extension") extension: RequestBody
    ): ResponseBody

    // Upload recipe image from URL
    @POST("api/recipes/{slug}/image")
    suspend fun uploadRecipeImageFromUrl(
        @Path("slug") slug: String,
        @Body request: UploadImageFromUrlRequest
    ): ResponseBody

    // Get all units
    @GET("api/units")
    suspend fun getUnits(
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = -1  // -1 gets all units
    ): UnitListResponse

    // Create new unit
    @POST("api/units")
    suspend fun createUnit(@Body request: CreateUnitRequest): RecipeUnit

    // Update unit
    @PUT("api/units/{id}")
    suspend fun updateUnit(@Path("id") id: String, @Body request: CreateUnitRequest): RecipeUnit

    // Delete unit
    @DELETE("api/units/{id}")
    suspend fun deleteUnit(@Path("id") id: String)

    // Get all foods
    @GET("api/foods")
    suspend fun getFoods(
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = -1  // -1 gets all foods
    ): FoodListResponse

    // Create new food
    @POST("api/foods")
    suspend fun createFood(@Body request: CreateFoodRequest): Food

    // Delete food
    @DELETE("api/foods/{id}")
    suspend fun deleteFood(@Path("id") id: String)

    // Get all tags
    @GET("api/organizers/tags")
    suspend fun getTags(
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = -1
    ): TagListResponse

    // Create new tag
    @POST("api/organizers/tags")
    suspend fun createTag(@Body request: CreateTagRequest): Tag

    // Update tag
    @PUT("api/organizers/tags/{id}")
    suspend fun updateTag(@Path("id") id: String, @Body tag: Tag): Tag

    // Delete tag
    @DELETE("api/organizers/tags/{id}")
    suspend fun deleteTag(@Path("id") id: String)
}
