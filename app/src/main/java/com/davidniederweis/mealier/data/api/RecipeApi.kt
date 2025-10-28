package com.davidniederweis.mealier.data.api

import com.davidniederweis.mealier.data.model.category.CategoryListResponse
import com.davidniederweis.mealier.data.model.cookbook.Cookbook
import com.davidniederweis.mealier.data.model.cookbook.CookbookListResponse
import com.davidniederweis.mealier.data.model.cookbook.UpdateCookbook
import com.davidniederweis.mealier.data.model.food.CreateFoodRequest
import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.model.food.FoodListResponse
import com.davidniederweis.mealier.data.model.household.HouseholdListResponse
import com.davidniederweis.mealier.data.model.recipe.*
import com.davidniederweis.mealier.data.model.tag.CreateTagRequest
import com.davidniederweis.mealier.data.model.tag.Tag
import com.davidniederweis.mealier.data.model.tag.TagListResponse
import com.davidniederweis.mealier.data.model.tool.ToolListResponse
import com.davidniederweis.mealier.data.model.unit.CreateUnitRequest
import com.davidniederweis.mealier.data.model.unit.RecipeUnit
import com.davidniederweis.mealier.data.model.unit.UnitListResponse
import com.davidniederweis.mealier.data.model.CreateCookBook
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface RecipeApi {
    @GET("api/recipes")
    suspend fun getRecipes(
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = 50,
        @Query("orderBy") orderBy: String? = null,
        @Query("orderDirection") orderDirection: String? = null,
        @Query("orderByNullPosition") orderByNullPosition: String? = null,
        @Query("search") search: String? = null,
        @Query("categories") categories: String? = null,
        @Query("tags") tags: String? = null,
        @Query("tools") tools: String? = null,
        @Query("foods") foods: String? = null,
        @Query("cookbook") cookbookSlug: String? = null,
        @Query("requireAllCategories") requireAllCategories: Boolean = false,
        @Query("requireAllTags") requireAllTags: Boolean = false,
        @Query("requireAllTools") requireAllTools: Boolean = false,
        @Query("requireAllFoods") requireAllFoods: Boolean = false
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
    @PATCH("api/recipes/{slug}")
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

    // Get all categories
    @GET("api/organizers/categories")
    suspend fun getCategories(
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = -1
    ): CategoryListResponse

    // Get all tools
    @GET("api/organizers/tools")
    suspend fun getTools(
        @Query("page") page: Int = 1,
        @Query(
            "perPage"
        ) perPage: Int = -1
    ): ToolListResponse

    // Get all households
    @GET("api/groups/households")
    suspend fun getHouseholds(
        @Query("page") page: Int = 1,
        @Query("perPage") perPage: Int = -1
    ): HouseholdListResponse

    @POST("api/households/cookbooks")
    suspend fun createCookbook(@Body cookbook: CreateCookBook): ResponseBody

    @GET("api/households/cookbooks")
    suspend fun getCookbooks(): CookbookListResponse

    @GET("api/households/cookbooks/{id}")
    suspend fun getCookbook(@Path("id") id: String): Cookbook

    @PUT("api/households/cookbooks/{id}")
    suspend fun updateCookbook(@Path("id") id: String, @Body cookbook: UpdateCookbook): ResponseBody
}
