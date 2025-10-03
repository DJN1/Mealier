package com.davidniederweis.mealier.data.repository

import com.davidniederweis.mealier.data.api.RecipeApi
import com.davidniederweis.mealier.data.model.food.CreateFoodRequest
import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.model.recipe.*
import com.davidniederweis.mealier.data.model.unit.CreateUnitRequest
import com.davidniederweis.mealier.data.model.unit.RecipeUnit
import com.davidniederweis.mealier.util.Logger
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class RecipeRepository(
    private val recipeApi: RecipeApi
) {
    // Existing methods
    suspend fun getAllRecipes(
        page: Int = 1,
        perPage: Int = 50,
        search: String? = null
    ): List<RecipeSummary> {
        return recipeApi.getRecipes(
            page = page,
            perPage = perPage,
            search = search
        ).items
    }

    suspend fun getRecipeBySlug(slug: String): RecipeDetail {
        return recipeApi.getRecipeDetail(slug)
    }

    suspend fun searchRecipes(
        query: String,
        page: Int = 1,
        perPage: Int = 50
    ): List<RecipeSummary> {
        return recipeApi.getRecipes(
            page = page,
            perPage = perPage,
            search = query
        ).items
    }

    // New methods for recipe creation

    // Get all units
    suspend fun getUnits(): List<RecipeUnit> {
        return try {
            Logger.d("RecipeRepository", "Fetching units")
            val response = recipeApi.getUnits()
            Logger.i("RecipeRepository", "Successfully fetched ${response.items.size} units")
            response.items
        } catch (e: Exception) {
            Logger.e("RecipeRepository", "Error fetching units: ${e.message}", e)
            throw e
        }
    }

    // Create new unit
    suspend fun createUnit(request: CreateUnitRequest): RecipeUnit {
        return try {
            Logger.d("RecipeRepository", "Creating unit: ${request.name}")
            val unit = recipeApi.createUnit(request)
            Logger.i("RecipeRepository", "Successfully created unit: ${unit.name}")
            unit
        } catch (e: Exception) {
            Logger.e("RecipeRepository", "Error creating unit: ${e.message}", e)
            throw e
        }
    }

    // Get all foods
    suspend fun getFoods(): List<Food> {
        return try {
            Logger.d("RecipeRepository", "Fetching foods")
            val response = recipeApi.getFoods()
            Logger.i("RecipeRepository", "Successfully fetched ${response.items.size} foods")
            response.items
        } catch (e: Exception) {
            Logger.e("RecipeRepository", "Error fetching foods: ${e.message}", e)
            throw e
        }
    }

    // Create new food
    suspend fun createFood(request: CreateFoodRequest): Food {
        return try {
            Logger.d("RecipeRepository", "Creating food: ${request.name}")
            val food = recipeApi.createFood(request)
            Logger.i("RecipeRepository", "Successfully created food: ${food.name}")
            food
        } catch (e: Exception) {
            Logger.e("RecipeRepository", "Error creating food: ${e.message}", e)
            throw e
        }
    }

    // Create recipe manually
    suspend fun createRecipe(request: CreateRecipeRequest): RecipeDetail {
        return try {
            Logger.d("RecipeRepository", "Creating recipe: ${request.name}")
            val recipe = recipeApi.createRecipe(request)
            Logger.i("RecipeRepository", "Successfully created recipe: ${recipe.name}")
            recipe
        } catch (e: Exception) {
            Logger.e("RecipeRepository", "Error creating recipe: ${e.message}", e)
            throw e
        }
    }

    // Create recipe from URL
    suspend fun createRecipeFromUrl(url: String): RecipeDetail {
        return try {
            Logger.d("RecipeRepository", "Creating recipe from URL: $url")
            val request = CreateRecipeFromUrlRequest(url = url, includeTags = true)
            val recipe = recipeApi.createRecipeFromUrl(request)
            Logger.i("RecipeRepository", "Successfully created recipe from URL: ${recipe.name}")
            recipe
        } catch (e: Exception) {
            Logger.e("RecipeRepository", "Error creating recipe from URL: ${e.message}", e)
            throw e
        }
    }

    // Update recipe
    suspend fun updateRecipe(slug: String, recipe: RecipeDetail): RecipeDetail {
        return try {
            Logger.d("RecipeRepository", "Updating recipe: $slug")
            Logger.d("RecipeRepository", "Recipe details - name: ${recipe.name}, ingredients: ${recipe.recipeIngredient.size}, instructions: ${recipe.recipeInstructions?.size}")
            val updated = recipeApi.updateRecipe(slug, recipe)
            Logger.i("RecipeRepository", "Successfully updated recipe: ${updated.name}")
            updated
        } catch (e: retrofit2.HttpException) {
            val errorBody = e.response()?.errorBody()?.string()
            Logger.e("RecipeRepository", "HTTP ${e.code()} Error updating recipe. Response: $errorBody", e)
            throw e
        } catch (e: Exception) {
            Logger.e("RecipeRepository", "Error updating recipe: ${e.message}", e)
            throw e
        }
    }

    // Upload recipe image from file
    suspend fun uploadRecipeImage(slug: String, imageFile: File): Boolean {
        return try {
            Logger.d("RecipeRepository", "Uploading image for recipe: $slug")
            
            // Extract file extension
            val extension = imageFile.extension.lowercase()
            Logger.d("RecipeRepository", "Image extension: $extension")
            
            val imageRequestBody = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", imageFile.name, imageRequestBody)
            
            // Create extension as a form field
            val extensionRequestBody = extension.toRequestBody("text/plain".toMediaTypeOrNull())
            
            recipeApi.uploadRecipeImage(slug, imagePart, extensionRequestBody)
            Logger.i("RecipeRepository", "Successfully uploaded image for recipe: $slug")
            true
        } catch (e: Exception) {
            Logger.e("RecipeRepository", "Error uploading image: ${e.message}", e)
            throw e
        }
    }

    // Upload recipe image from URL
    suspend fun uploadRecipeImageFromUrl(slug: String, imageUrl: String): Boolean {
        return try {
            Logger.d("RecipeRepository", "Uploading image from URL for recipe: $slug")
            Logger.d("RecipeRepository", "Image URL: $imageUrl")
            
            val request = UploadImageFromUrlRequest(url = imageUrl)
            recipeApi.uploadRecipeImageFromUrl(slug, request)
            Logger.i("RecipeRepository", "Successfully uploaded image from URL for recipe: $slug")
            true
        } catch (e: Exception) {
            Logger.e("RecipeRepository", "Error uploading image from URL: ${e.message}", e)
            throw e
        }
    }
}
