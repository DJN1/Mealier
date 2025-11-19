package com.davidniederweis.mealier.ui.viewmodel.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.food.CreateFoodRequest
import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.model.ingredient.CreateIngredientFoodRequest
import com.davidniederweis.mealier.data.model.ingredient.CreateIngredientRequest
import com.davidniederweis.mealier.data.model.ingredient.CreateIngredientUnitRequest
import com.davidniederweis.mealier.data.model.instruction.CreateInstructionRequest
import com.davidniederweis.mealier.data.model.nutrition.CreateNutritionRequest
import com.davidniederweis.mealier.data.model.recipe.*
import com.davidniederweis.mealier.data.model.unit.CreateUnitRequest
import com.davidniederweis.mealier.data.model.unit.RecipeUnit
import com.davidniederweis.mealier.data.repository.RecipeRepository
import com.davidniederweis.mealier.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class AddRecipeViewModel(
    private val repository: RecipeRepository
) : ViewModel(), RecipeFormViewModel {

    // State for the creation process
    private val _creationState = MutableStateFlow<RecipeCreationState>(RecipeCreationState.Idle)
    val creationState: StateFlow<RecipeCreationState> = _creationState.asStateFlow()

    // Available units and foods
    private val _units = MutableStateFlow<List<RecipeUnit>>(emptyList())
    override val units: StateFlow<List<RecipeUnit>> = _units.asStateFlow()

    private val _foods = MutableStateFlow<List<Food>>(emptyList())
    override val foods: StateFlow<List<Food>> = _foods.asStateFlow()

    // Manual recipe form state
    private val _recipeName = MutableStateFlow("")
    override val recipeName: StateFlow<String> = _recipeName.asStateFlow()

    private val _recipeDescription = MutableStateFlow("")
    override val recipeDescription: StateFlow<String> = _recipeDescription.asStateFlow()

    private val _ingredients = MutableStateFlow(listOf(IngredientInput()))
    override val ingredients: StateFlow<List<IngredientInput>> = _ingredients.asStateFlow()

    private val _instructions = MutableStateFlow(listOf(InstructionInput()))
    override val instructions: StateFlow<List<InstructionInput>> = _instructions.asStateFlow()

    private val _servings = MutableStateFlow("")
    override val servings: StateFlow<String> = _servings.asStateFlow()

    private val _prepTime = MutableStateFlow("")
    override val prepTime: StateFlow<String> = _prepTime.asStateFlow()

    private val _cookTime = MutableStateFlow("")
    override val cookTime: StateFlow<String> = _cookTime.asStateFlow()

    private val _totalTime = MutableStateFlow("")
    override val totalTime: StateFlow<String> = _totalTime.asStateFlow()

    // Nutrition info
    private val _nutrition = MutableStateFlow(NutritionInput())
    override val nutrition: StateFlow<NutritionInput> = _nutrition.asStateFlow()

    // Image handling
    private val _imageFile = MutableStateFlow<File?>(null)
    override val imageFile: StateFlow<File?> = _imageFile.asStateFlow()

    private val _imageUrl = MutableStateFlow("")
    override val imageUrl: StateFlow<String> = _imageUrl.asStateFlow()

    // URL import
    private val _recipeUrl = MutableStateFlow("")
    val recipeUrl: StateFlow<String> = _recipeUrl.asStateFlow()

    init {
        loadUnitsAndFoods()
    }

    // Load units and foods on init
    private fun loadUnitsAndFoods() {
        viewModelScope.launch {
            try {
                Logger.d("AddRecipeViewModel", "Loading units and foods")
                val unitsResult = repository.getUnits()
                val foodsResult = repository.getFoods()

                _units.value = unitsResult.sortedBy { it.name }
                _foods.value = foodsResult.sortedBy { it.name }

                Logger.i("AddRecipeViewModel", "Loaded ${unitsResult.size} units and ${foodsResult.size} foods")
            } catch (e: Exception) {
                Logger.e("AddRecipeViewModel", "Error loading units and foods: ${e.message}", e)
            }
        }
    }

    // Manual recipe form updates
    override fun updateRecipeName(name: String) {
        _recipeName.value = name
    }

    override fun updateRecipeDescription(description: String) {
        _recipeDescription.value = description
    }

    override fun updateServings(servings: String) {
        _servings.value = servings
    }

    override fun updatePrepTime(time: String) {
        _prepTime.value = time
    }

    override fun updateCookTime(time: String) {
        _cookTime.value = time
    }

    override fun updateTotalTime(time: String) {
        _totalTime.value = time
    }

    override fun updateNutrition(nutrition: NutritionInput) {
        _nutrition.value = nutrition
    }

    // Ingredient management
    override fun addIngredient() {
        _ingredients.value = _ingredients.value + IngredientInput()
    }

    override fun removeIngredient(index: Int) {
        if (_ingredients.value.size > 1) {
            _ingredients.value = _ingredients.value.filterIndexed { i, _ -> i != index }
        }
    }

    override fun updateIngredient(index: Int, ingredient: IngredientInput) {
        _ingredients.value = _ingredients.value.mapIndexed { i, ing ->
            if (i == index) ingredient else ing
        }
    }

    // Instruction management
    override fun addInstruction() {
        _instructions.value = _instructions.value + InstructionInput()
    }

    override fun removeInstruction(index: Int) {
        if (_instructions.value.size > 1) {
            _instructions.value = _instructions.value.filterIndexed { i, _ -> i != index }
        }
    }

    override fun updateInstruction(index: Int, instruction: InstructionInput) {
        _instructions.value = _instructions.value.mapIndexed { i, inst ->
            if (i == index) instruction else inst
        }
    }

    // Image handling
    override fun setImageFile(file: File?) {
        _imageFile.value = file
        if (file != null) {
            _imageUrl.value = "" // Clear URL if file is set
        }
    }

    override fun setImageUrl(url: String) {
        _imageUrl.value = url
        if (url.isNotBlank()) {
            _imageFile.value = null // Clear file if URL is set
        }
    }

    // Create new unit
    override fun createUnit(name: String, onSuccess: (RecipeUnit) -> Unit) {
        viewModelScope.launch {
            try {
                Logger.d("AddRecipeViewModel", "Creating new unit: $name")
                val request = CreateUnitRequest(name = name)
                val newUnit = repository.createUnit(request)

                // Add to list and sort
                _units.value = (_units.value + newUnit).sortedBy { it.name }
                Logger.i("AddRecipeViewModel", "Successfully created unit: ${newUnit.name}")
                onSuccess(newUnit)
            } catch (e: Exception) {
                Logger.e("AddRecipeViewModel", "Error creating unit: ${e.message}", e)
            }
        }
    }

    // Create new food
    override fun createFood(name: String, onSuccess: (Food) -> Unit) {
        viewModelScope.launch {
            try {
                Logger.d("AddRecipeViewModel", "Creating new food: $name")
                val request = CreateFoodRequest(name = name)
                val newFood = repository.createFood(request)

                // Add to list and sort
                _foods.value = (_foods.value + newFood).sortedBy { it.name }
                Logger.i("AddRecipeViewModel", "Successfully created food: ${newFood.name}")
                onSuccess(newFood)
            } catch (e: Exception) {
                Logger.e("AddRecipeViewModel", "Error creating food: ${e.message}", e)
            }
        }
    }

    // Create recipe manually
    fun createManualRecipe() {
        if (_recipeName.value.isBlank()) {
            _creationState.value = RecipeCreationState.Error("Recipe name is required")
            return
        }

        viewModelScope.launch {
            _creationState.value = RecipeCreationState.Loading
            try {
                Logger.d("AddRecipeViewModel", "Creating manual recipe: ${_recipeName.value}")

                // Build ingredient list
                val ingredientRequests = _ingredients.value
                    .filter { it.food != null }
                    .map { ingredient ->
                        CreateIngredientRequest(
                            quantity = ingredient.quantity.toDoubleOrNull(),
                            unit = ingredient.unit?.let { CreateIngredientUnitRequest(it.id!!, it.name) },
                            food = CreateIngredientFoodRequest(ingredient.food!!.id!!, ingredient.food.name),
                            note = ingredient.note.takeIf { it.isNotBlank() }
                        )
                    }

                // Build instruction list
                val instructionRequests = _instructions.value
                    .filter { it.title.isNotBlank() || it.text.isNotBlank() }
                    .map { instruction ->
                        CreateInstructionRequest(
                            title = instruction.title.takeIf { it.isNotBlank() },
                            text = instruction.text.takeIf { it.isNotBlank() },
                            summary = instruction.text.takeIf { it.isNotBlank() } // Use text as summary too
                        )
                    }

                // Build nutrition request if any fields are filled
                val nutritionRequest = if (_nutrition.value.hasAnyValue()) {
                    CreateNutritionRequest(
                        calories = _nutrition.value.calories.takeIf { it.isNotBlank() },
                        fatContent = _nutrition.value.fatContent.takeIf { it.isNotBlank() },
                        proteinContent = _nutrition.value.proteinContent.takeIf { it.isNotBlank() },
                        carbohydrateContent = _nutrition.value.carbohydrateContent.takeIf { it.isNotBlank() }
                    )
                } else null

                val request = CreateRecipeRequest(
                    name = _recipeName.value,
                    description = _recipeDescription.value.takeIf { it.isNotBlank() },
                    recipeIngredient = ingredientRequests,
                    recipeInstructions = instructionRequests,
                    recipeServings = _servings.value.toDoubleOrNull(),
                    prepTime = _prepTime.value.takeIf { it.isNotBlank() },
                    cookTime = _cookTime.value.takeIf { it.isNotBlank() },
                    totalTime = _totalTime.value.takeIf { it.isNotBlank() },
                    nutrition = nutritionRequest
                )

                val recipe = repository.createRecipe(request)
                Logger.i("AddRecipeViewModel", "Successfully created recipe: ${recipe.name}")

                // Upload image if provided
                if (_imageFile.value != null) {
                    try {
                        Logger.d("AddRecipeViewModel", "Uploading image for recipe: ${recipe.slug}")
                        repository.uploadRecipeImage(recipe.slug, _imageFile.value!!)
                        Logger.i("AddRecipeViewModel", "Successfully uploaded image")
                    } catch (e: Exception) {
                        Logger.w("AddRecipeViewModel", "Failed to upload image: ${e.message}", e)
                        // Don't fail the whole operation if image upload fails
                    }
                }

                _creationState.value = RecipeCreationState.Success(recipe)
            } catch (e: Exception) {
                Logger.e("AddRecipeViewModel", "Error creating recipe: ${e.message}", e)
                _creationState.value = RecipeCreationState.Error(
                    e.message ?: "Failed to create recipe"
                )
            }
        }
    }

    // Create recipe from URL
    fun updateRecipeUrl(url: String) {
        _recipeUrl.value = url
    }

    fun createRecipeFromUrl() {
        if (_recipeUrl.value.isBlank()) {
            _creationState.value = RecipeCreationState.Error("Recipe URL is required")
            return
        }

        viewModelScope.launch {
            _creationState.value = RecipeCreationState.Loading
            try {
                Logger.d("AddRecipeViewModel", "Creating recipe from URL: ${_recipeUrl.value}")
                val recipe = repository.createRecipeFromUrl(_recipeUrl.value)
                Logger.i("AddRecipeViewModel", "Successfully created recipe from URL: ${recipe.name}")
                _creationState.value = RecipeCreationState.Success(recipe)
            } catch (e: Exception) {
                Logger.e("AddRecipeViewModel", "Error creating recipe from URL: ${e.message}", e)
                _creationState.value = RecipeCreationState.Error(
                    e.message ?: "Failed to import recipe from URL"
                )
            }
        }
    }

}

// State classes
sealed class RecipeCreationState {
    object Idle : RecipeCreationState()
    object Loading : RecipeCreationState()
    data class Success(val recipe: RecipeDetail) : RecipeCreationState()
    data class Error(val message: String) : RecipeCreationState()
}

// Input classes for form state
data class IngredientInput(
    val quantity: String = "",
    val unit: RecipeUnit? = null,
    val food: Food? = null,
    val note: String = "",
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
