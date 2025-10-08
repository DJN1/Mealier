package com.davidniederweis.mealier.ui.viewmodel.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.recipe.RecipeSummary
import com.davidniederweis.mealier.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
class RecipeViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _recipeListState = MutableStateFlow<RecipeListState>(RecipeListState.Idle)
    val recipeListState: StateFlow<RecipeListState> = _recipeListState.asStateFlow()

    private val _recipeDetailState = MutableStateFlow<RecipeDetailState>(RecipeDetailState.Idle)
    val recipeDetailState: StateFlow<RecipeDetailState> = _recipeDetailState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var currentPage = 1
    private val perPage = 50
    private var isLoadingMore = false
    private val allRecipes = mutableListOf<RecipeSummary>()

    init {
        loadRecipes()
    }

    fun loadRecipes(refresh: Boolean = false) {
        if (refresh) {
            currentPage = 1
            allRecipes.clear()
        }

        viewModelScope.launch {
            _recipeListState.value = RecipeListState.Loading
            try {
                val recipes = repository.getAllRecipes(
                    page = currentPage,
                    perPage = perPage,
                    search = _searchQuery.value.ifBlank { null }
                )

                if (refresh) {
                    allRecipes.clear()
                }
                allRecipes.addAll(recipes)

                _recipeListState.value = RecipeListState.Success(allRecipes.toList())
            } catch (e: Exception) {
                _recipeListState.value = RecipeListState.Error(
                    e.message ?: "Failed to load recipes"
                )
            }
        }
    }

    fun loadMoreRecipes() {
        if (isLoadingMore) return

        isLoadingMore = true
        currentPage++

        viewModelScope.launch {
            try {
                val recipes = repository.getAllRecipes(
                    page = currentPage,
                    perPage = perPage,
                    search = _searchQuery.value.ifBlank { null }
                )

                allRecipes.addAll(recipes)
                _recipeListState.value = RecipeListState.Success(allRecipes.toList())
            } catch (e: Exception) {
                // Don't change state on pagination error, just reset page
                currentPage--
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun retryLoadRecipes() {
        loadRecipes(refresh = true)
    }

    fun loadRecipeDetail(slug: String) {
        viewModelScope.launch {
            _recipeDetailState.value = RecipeDetailState.Loading
            try {
                val recipe = repository.getRecipeBySlug(slug)
                _recipeDetailState.value = RecipeDetailState.Success(recipe)
            } catch (e: Exception) {
                _recipeDetailState.value = RecipeDetailState.Error(
                    e.message ?: "Failed to load recipe details"
                )
            }
        }
    }

    fun searchRecipes(query: String) {
        _searchQuery.value = query
        currentPage = 1
        allRecipes.clear()

        viewModelScope.launch {
            _recipeListState.value = RecipeListState.Loading
            try {
                val recipes = if (query.isBlank()) {
                    repository.getAllRecipes(page = 1, perPage = perPage)
                } else {
                    repository.searchRecipes(query, page = 1, perPage = perPage)
                }

                allRecipes.clear()
                allRecipes.addAll(recipes)
                _recipeListState.value = RecipeListState.Success(allRecipes.toList())
            } catch (e: Exception) {
                _recipeListState.value = RecipeListState.Error(
                    e.message ?: "Failed to search recipes"
                )
            }
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        loadRecipes(refresh = true)
    }

    fun refresh() {
        loadRecipes(refresh = true)
    }

    fun deleteRecipe(slug: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteRecipe(slug)
                onSuccess()
            } catch (e: Exception) {
                onError(e.message ?: "Failed to delete recipe")
            }
        }
    }
}