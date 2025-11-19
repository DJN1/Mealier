package com.davidniederweis.mealier.ui.viewmodel.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.food.CreateFoodRequest
import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.model.ingredient.RecipeIngredient
import com.davidniederweis.mealier.data.model.ingredient.RecipeIngredientFood
import com.davidniederweis.mealier.data.model.ingredient.RecipeIngredientUnit
import com.davidniederweis.mealier.data.model.recipe.*
import com.davidniederweis.mealier.data.model.unit.CreateUnitRequest
import com.davidniederweis.mealier.data.model.unit.RecipeUnit
import com.davidniederweis.mealier.data.repository.RecipeRepository
import com.davidniederweis.mealier.util.Logger
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

import java.util.UUID

class EditRecipeViewModel(
    private val repository: RecipeRepository
) : ViewModel(), RecipeFormViewModel {

    // State for the update process
    private val _updateState = MutableStateFlow<RecipeCreationState>(RecipeCreationState.Idle)
    val updateState: StateFlow<RecipeCreationState> = _updateState.asStateFlow()

    // Loading state for fetching existing recipe
    private val _loadingState = MutableStateFlow<LoadingState>(LoadingState.Idle)
    val loadingState: StateFlow<LoadingState> = _loadingState.asStateFlow()

    // Available units and foods
    private val _units = MutableStateFlow<List<RecipeUnit>>(emptyList())
    override val units: StateFlow<List<RecipeUnit>> = _units.asStateFlow()

    private val _foods = MutableStateFlow<List<Food>>(emptyList())
    override val foods: StateFlow<List<Food>> = _foods.asStateFlow()

    // Recipe form state
    private val _recipeSlug = MutableStateFlow("")

    // Store the original recipe to update
    private var originalRecipe: RecipeDetail? = null

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

    init {
        loadUnitsAndFoods()
    }

    // Load units and foods on init
    private fun loadUnitsAndFoods() {
        viewModelScope.launch {
            try {
                Logger.d("EditRecipeViewModel", "Loading units and foods")
                val unitsResult = repository.getUnits()
                val foodsResult = repository.getFoods()

                _units.value = unitsResult.sortedBy { it.name }
                _foods.value = foodsResult.sortedBy { it.name }

                Logger.i("EditRecipeViewModel", "Loaded ${unitsResult.size} units and ${foodsResult.size} foods")
            } catch (e: Exception) {
                Logger.e("EditRecipeViewModel", "Error loading units and foods: ${e.message}", e)
            }
        }
    }

    // Load existing recipe for editing
    fun loadRecipe(slug: String) {
        viewModelScope.launch {
            _loadingState.value = LoadingState.Loading
            try {
                Logger.d("EditRecipeViewModel", "Loading recipe for editing: $slug")
                
                // Load units, foods, and recipe in parallel
                val unitsDeferred = async { repository.getUnits() }
                val foodsDeferred = async { repository.getFoods() }
                val recipeDeferred = async { repository.getRecipeBySlug(slug) }
                
                val units = unitsDeferred.await().sortedBy { it.name }
                val foods = foodsDeferred.await().sortedBy { it.name }
                val recipe = recipeDeferred.await()
                
                _units.value = units
                _foods.value = foods
                
                // Store the original recipe
                originalRecipe = recipe

                _recipeSlug.value = slug
                _recipeName.value = recipe.name ?: ""
                _recipeDescription.value = recipe.description ?: ""
                _servings.value = recipe.recipeServings.toString()
                _prepTime.value = recipe.prepTime ?: ""
                _cookTime.value = recipe.cookTime ?: ""
                _totalTime.value = recipe.totalTime ?: ""

                // Load ingredients with proper unit and food references
                _ingredients.value = if (recipe.recipeIngredient.isNotEmpty()) {
                    recipe.recipeIngredient.map { ingredient ->
                        Logger.d("EditRecipeViewModel", "Loading ingredient - quantity: ${ingredient.quantity}, food: ${ingredient.food?.name}")
                        IngredientInput(
                            quantity = ingredient.quantity.toString(),
                            unit = ingredient.unit?.let { unit ->
                                units.find { it.id == unit.id }
                            },
                            food = ingredient.food?.let { food ->
                                foods.find { it.id == food.id }
                            },
                            note = ingredient.note ?: "",
                            referenceId = ingredient.referenceId
                        )
                    }
                } else {
                    listOf(IngredientInput())
                }

                // Load instructions
                Logger.d("EditRecipeViewModel", "Recipe has ${recipe.recipeInstructions?.size ?: 0} instructions")
                _instructions.value = if (!recipe.recipeInstructions.isNullOrEmpty()) {
                    recipe.recipeInstructions.mapIndexed { index, instruction ->
                        Logger.d("EditRecipeViewModel", "Instruction $index - title: '${instruction.title}', text: '${instruction.text}', summary: '${instruction.summary}'")
                        InstructionInput(
                            title = instruction.title ?: "",
                            // Use text if available, otherwise fall back to summary
                            text = instruction.text?.takeIf { it.isNotBlank() } ?: instruction.summary ?: ""
                        )
                    }
                } else {
                    Logger.d("EditRecipeViewModel", "No instructions found, using empty list")
                    listOf(InstructionInput())
                }

                // Load nutrition
                recipe.nutrition?.let { nutrition ->
                    _nutrition.value = NutritionInput(
                        calories = nutrition.calories ?: "",
                        fatContent = nutrition.fatContent ?: "",
                        proteinContent = nutrition.proteinContent ?: "",
                        carbohydrateContent = nutrition.carbohydrateContent ?: ""
                    )
                }

                _loadingState.value = LoadingState.Success
                Logger.i("EditRecipeViewModel", "Successfully loaded recipe for editing")
            } catch (e: Exception) {
                Logger.e("EditRecipeViewModel", "Error loading recipe: ${e.message}", e)
                _loadingState.value = LoadingState.Error(e.message ?: "Failed to load recipe")
            }
        }
    }

    // Update methods - same as AddRecipeViewModel
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
                Logger.d("EditRecipeViewModel", "Creating new unit: $name")
                val request = CreateUnitRequest(name = name)
                val newUnit = repository.createUnit(request)

                // Add to list and sort
                _units.value = (_units.value + newUnit).sortedBy { it.name }
                Logger.i("EditRecipeViewModel", "Successfully created unit: ${newUnit.name}")
                onSuccess(newUnit)
            } catch (e: Exception) {
                Logger.e("EditRecipeViewModel", "Error creating unit: ${e.message}", e)
            }
        }
    }

    // Create new food
    override fun createFood(name: String, onSuccess: (Food) -> Unit) {
        viewModelScope.launch {
            try {
                Logger.d("EditRecipeViewModel", "Creating new food: $name")
                val request = CreateFoodRequest(name = name)
                val newFood = repository.createFood(request)

                // Add to list and sort
                _foods.value = (_foods.value + newFood).sortedBy { it.name }
                Logger.i("EditRecipeViewModel", "Successfully created food: ${newFood.name}")
                onSuccess(newFood)
            } catch (e: Exception) {
                Logger.e("EditRecipeViewModel", "Error creating food: ${e.message}", e)
            }
        }
    }

    // Update recipe
    fun updateRecipe() {
        val original = originalRecipe
        if (original == null) {
            _updateState.value = RecipeCreationState.Error("Recipe not loaded")
            return
        }
        
        if (_recipeName.value.isBlank()) {
            _updateState.value = RecipeCreationState.Error("Recipe name is required")
            return
        }

        viewModelScope.launch {
            _updateState.value = RecipeCreationState.Loading
            try {
                val updateRequest = original.copy(
                    name = _recipeName.value,
                    description = _recipeDescription.value.takeIf { it.isNotBlank() },
                    recipeServings = _servings.value.toDoubleOrNull() ?: original.recipeServings,
                    prepTime = _prepTime.value.takeIf { it.isNotBlank() },
                    cookTime = _cookTime.value.takeIf { it.isNotBlank() },
                    totalTime = _totalTime.value.takeIf { it.isNotBlank() },
                    recipeIngredient = _ingredients.value.mapNotNull { input ->
                        if (input.food != null) {
                            RecipeIngredient(
                                title = null,
                                note = input.note.takeIf { it.isNotBlank() },
                                unit = input.unit?.let { unit ->
                                    RecipeIngredientUnit(
                                        id = unit.id,
                                        name = unit.name,
                                        description = unit.description ?: "",
                                        pluralName = unit.pluralName,
                                        abbreviation = unit.abbreviation ?: "",
                                        pluralAbbreviation = unit.pluralAbbreviation,
                                        fraction = unit.fraction ?: false,
                                        useAbbreviation = unit.useAbbreviation ?: false,
                                        aliases = emptyList()
                                    )
                                },
                                food = RecipeIngredientFood(
                                    id = input.food.id,
                                    name = input.food.name,
                                    description = input.food.description ?: "",
                                    pluralName = input.food.pluralName,
                                    aliases = emptyList(),
                                    householdsWithIngredientFood = emptyList()
                                ),
                                disableAmount = false,
                                quantity = input.quantity.toDoubleOrNull() ?: 0.0,
                                display = "${input.quantity} ${input.unit?.name ?: ""} ${input.food.name}",
                                originalText = "",
                                referenceId = input.referenceId ?: UUID.randomUUID().toString().replace("-", "")
                            )
                        } else {
                            null
                        }
                    },
                    recipeInstructions = _instructions.value.mapNotNull { input ->
                        if (input.text.isNotBlank()) {
                            RecipeInstruction(
                                id = null,
                                title = input.title.takeIf { it.isNotBlank() } ?: "",
                                text = input.text,
                                summary = null,
                                ingredientReferences = emptyList()
                            )
                        } else {
                            null
                        }
                    }
                )

                Logger.d("EditRecipeViewModel", "Updating recipe with ${updateRequest.recipeIngredient.size} ingredients, ${updateRequest.recipeInstructions?.size} instructions")
                val recipe = repository.updateRecipe(_recipeSlug.value, updateRequest)
                Logger.i("EditRecipeViewModel", "Successfully updated recipe: ${recipe.name}")

                // Upload image if provided (file or URL)
                when {
                    _imageFile.value != null -> {
                        try {
                            Logger.d("EditRecipeViewModel", "Uploading image file for recipe: ${recipe.slug}")
                            repository.uploadRecipeImage(recipe.slug, _imageFile.value!!)
                            Logger.i("EditRecipeViewModel", "Successfully uploaded image file")
                        } catch (e: Exception) {
                            Logger.w("EditRecipeViewModel", "Failed to upload image file: ${e.message}", e)
                            // Don't fail the whole operation if image upload fails
                        }
                    }
                    _imageUrl.value.isNotBlank() -> {
                        try {
                            Logger.d("EditRecipeViewModel", "Uploading image from URL for recipe: ${recipe.slug}")
                            repository.uploadRecipeImageFromUrl(recipe.slug, _imageUrl.value)
                            Logger.i("EditRecipeViewModel", "Successfully uploaded image from URL")
                        } catch (e: Exception) {
                            Logger.w("EditRecipeViewModel", "Failed to upload image from URL: ${e.message}", e)
                            // Don't fail the whole operation if image upload fails
                        }
                    }
                }

                _updateState.value = RecipeCreationState.Success(recipe)
            } catch (e: Exception) {
                Logger.e("EditRecipeViewModel", "Error updating recipe: ${e.message}", e)
                _updateState.value = RecipeCreationState.Error(
                    e.message ?: "Failed to update recipe"
                )
            }
        }
    }

}

// Loading state for initial recipe fetch
sealed class LoadingState {
    object Idle : LoadingState()
    object Loading : LoadingState()
    object Success : LoadingState()
    data class Error(val message: String) : LoadingState()
}
