package com.davidniederweis.mealier.ui.viewmodel.recipe

import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.model.unit.RecipeUnit
import kotlinx.coroutines.flow.StateFlow
import java.io.File

interface RecipeFormViewModel {
    val recipeName: StateFlow<String>
    val recipeDescription: StateFlow<String>
    val ingredients: StateFlow<List<IngredientInput>>
    val instructions: StateFlow<List<InstructionInput>>
    val servings: StateFlow<String>
    val prepTime: StateFlow<String>
    val cookTime: StateFlow<String>
    val totalTime: StateFlow<String>
    val nutrition: StateFlow<NutritionInput>
    val imageFile: StateFlow<File?>
    val imageUrl: StateFlow<String>
    val units: StateFlow<List<RecipeUnit>>
    val foods: StateFlow<List<Food>>

    fun updateRecipeName(name: String)
    fun updateRecipeDescription(description: String)
    fun updateServings(servings: String)
    fun updatePrepTime(time: String)
    fun updateCookTime(time: String)
    fun updateTotalTime(time: String)
    fun updateNutrition(nutrition: NutritionInput)
    fun addIngredient()
    fun removeIngredient(index: Int)
    fun updateIngredient(index: Int, ingredient: IngredientInput)
    fun addInstruction()
    fun removeInstruction(index: Int)
    fun updateInstruction(index: Int, instruction: InstructionInput)
    fun setImageFile(file: File?)
    fun setImageUrl(url: String)
    fun createUnit(name: String, onSuccess: (RecipeUnit) -> Unit)
    fun createFood(name: String, onSuccess: (Food) -> Unit)
}
