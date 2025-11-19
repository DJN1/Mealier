package com.davidniederweis.mealier.ui.viewmodel.recipe

import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.model.recipe.RecipeDetail
import com.davidniederweis.mealier.data.model.unit.RecipeUnit

import com.davidniederweis.mealier.data.model.parser.ParsedIngredient

// State classes
sealed class RecipeCreationState {
    object Idle : RecipeCreationState()
    object Loading : RecipeCreationState()
    data class Success(val recipe: RecipeDetail) : RecipeCreationState()
    data class Error(val message: String) : RecipeCreationState()
}

data class IngredientResolution(
    val index: Int,
    val parsed: ParsedIngredient,
    val originalInput: IngredientInput
)

// Input classes for form state
data class IngredientInput(
    val quantity: String = "",
    val unit: RecipeUnit? = null,
    val food: Food? = null,
    val note: String = "",
    val originalText: String = "",
    val referenceId: String? = null
)

data class InstructionInput(
    val title: String = "",
    val text: String = ""
)

data class NutritionInput(
    val calories: String = "",
    val fatContent: String = "",
    val proteinContent: String = "",
    val carbohydrateContent: String = ""
) {
    fun hasAnyValue(): Boolean {
        return calories.isNotBlank() ||
                fatContent.isNotBlank() ||
                proteinContent.isNotBlank() ||
                carbohydrateContent.isNotBlank()
    }
}
