package com.davidniederweis.mealier.ui.viewmodel.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.BuildConfig
import com.davidniederweis.mealier.data.api.UserApi
import com.davidniederweis.mealier.data.model.category.Category
import com.davidniederweis.mealier.data.model.recipe.RecipeCategory
import com.davidniederweis.mealier.data.model.recipe.RecipeDetail
import com.davidniederweis.mealier.data.model.recipe.RecipeSummary
import com.davidniederweis.mealier.data.model.recipe.RecipeTag
import com.davidniederweis.mealier.data.model.tag.Tag
import com.davidniederweis.mealier.data.preferences.ServerPreferences
import com.davidniederweis.mealier.data.repository.RecipeRepository
import com.davidniederweis.mealier.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RecipeViewModel(
    private val repository: RecipeRepository,
    private val serverPreferences: ServerPreferences,
    private val userApi: UserApi
) : ViewModel() {

    private val _recipeListState = MutableStateFlow<RecipeListState>(RecipeListState.Idle)
    val recipeListState: StateFlow<RecipeListState> = _recipeListState.asStateFlow()

    private val _recipeDetailState = MutableStateFlow<RecipeDetailState>(RecipeDetailState.Idle)
    val recipeDetailState: StateFlow<RecipeDetailState> = _recipeDetailState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _baseUrl = MutableStateFlow("")
    val baseUrl: StateFlow<String> = _baseUrl.asStateFlow()

    private val _allCategories = MutableStateFlow<List<Category>>(emptyList())
    val allCategories: StateFlow<List<Category>> = _allCategories.asStateFlow()

    private val _allTags = MutableStateFlow<List<Tag>>(emptyList())
    val allTags: StateFlow<List<Tag>> = _allTags.asStateFlow()

    private val _favoriteState = MutableStateFlow<Boolean?>(null)
    val favoriteState: StateFlow<Boolean?> = _favoriteState.asStateFlow()

    private val _favoriteInProgress = MutableStateFlow(false)
    val favoriteInProgress: StateFlow<Boolean> = _favoriteInProgress.asStateFlow()

    private val _favoriteRecipesState = MutableStateFlow<FavoriteRecipesState>(FavoriteRecipesState.Idle)
    val favoriteRecipesState: StateFlow<FavoriteRecipesState> = _favoriteRecipesState.asStateFlow()

    private var currentPage = 1
    private val perPage = 50
    private var isLoadingMore = false
    private val allRecipes = mutableListOf<RecipeSummary>()
    private val loadedRecipeIds = mutableSetOf<String>()
    private var activeCategoryIds: List<String> = emptyList()
    private var activeTagIds: List<String> = emptyList()
    private var isEndReached = false
    private var currentRecipeSlug: String? = null
    private var currentRecipeId: String? = null
    private var currentUserId: String? = null

    init {
        loadRecipes()
        loadFilterData()
        viewModelScope.launch {
            val url = serverPreferences.getServerUrlOnce()
            _baseUrl.value = url.ifBlank { BuildConfig.BASE_URL }
        }
        viewModelScope.launch {
            ensureCurrentUserId()
        }
    }

    private fun loadFilterData() {
        viewModelScope.launch {
            try {
                _allCategories.value = repository.getCategories()
                _allTags.value = repository.getTags()
            } catch (_: Exception) {
                // Handle error
            }
        }
    }

    fun loadRecipes(
        refresh: Boolean = false,
        categoryIds: List<String> = emptyList(),
        tagIds: List<String> = emptyList()
    ) {
        if (refresh) {
            currentPage = 1
            allRecipes.clear()
            loadedRecipeIds.clear()
            isLoadingMore = false
            isEndReached = false
        }

        activeCategoryIds = categoryIds
        activeTagIds = tagIds

        viewModelScope.launch {
            _recipeListState.value = RecipeListState.Loading
            try {
                val response = repository.getAllRecipes(
                    page = currentPage,
                    perPage = perPage,
                    search = _searchQuery.value.ifBlank { null },
                    categories = categoryIds.joinToString(",").ifBlank { null },
                    tags = tagIds.joinToString(",").ifBlank { null }
                )

                if (response.page == 1) {
                    allRecipes.clear()
                    loadedRecipeIds.clear()
                }

                currentPage = response.page

                val newRecipes = response.items.filter { recipe ->
                    if (loadedRecipeIds.contains(recipe.id)) {
                        false
                    } else {
                        loadedRecipeIds.add(recipe.id)
                        true
                    }
                }
                allRecipes.addAll(newRecipes)

                isEndReached = response.page >= response.totalPages || response.items.isEmpty()

                _recipeListState.value = RecipeListState.Success(
                    recipes = allRecipes.toList(),
                    hasMore = !isEndReached
                )
            } catch (e: Exception) {
                _recipeListState.value = RecipeListState.Error(
                    e.message ?: "Failed to load recipes"
                )
            }
        }
    }

    fun loadRecipesByCookbook(cookbookSlug: String) {
        viewModelScope.launch {
            _recipeListState.value = RecipeListState.Loading
            try {
                val recipes = repository.getRecipesByCookbook(cookbookSlug)
                allRecipes.clear()
                loadedRecipeIds.clear()
                allRecipes.addAll(recipes)
                loadedRecipeIds.addAll(recipes.map { it.id })
                currentPage = 1
                isEndReached = true
                isLoadingMore = false
                _recipeListState.value = RecipeListState.Success(recipes, hasMore = false)
            } catch (e: Exception) {
                _recipeListState.value = RecipeListState.Error(e.message ?: "Failed to load cookbook recipes")
            }
        }
    }

    fun clearRecipes() {
        allRecipes.clear()
        loadedRecipeIds.clear()
        currentPage = 1
        isLoadingMore = false
        isEndReached = false
        _recipeListState.value = RecipeListState.Success(emptyList(), hasMore = false)
    }

    fun loadMoreRecipes() {
        if (isLoadingMore || isEndReached) return

        val nextPage = currentPage + 1
        isLoadingMore = true

        viewModelScope.launch {
            try {
                val response = repository.getAllRecipes(
                    page = nextPage,
                    perPage = perPage,
                    search = _searchQuery.value.ifBlank { null },
                    categories = activeCategoryIds.joinToString(",").ifBlank { null },
                    tags = activeTagIds.joinToString(",").ifBlank { null }
                )

                val newRecipes = response.items.filter { recipe ->
                    if (loadedRecipeIds.contains(recipe.id)) {
                        false
                    } else {
                        loadedRecipeIds.add(recipe.id)
                        true
                    }
                }

                if (newRecipes.isNotEmpty()) {
                    allRecipes.addAll(newRecipes)
                    currentPage = response.page
                }

                isEndReached = response.page >= response.totalPages || newRecipes.isEmpty()

                _recipeListState.value = RecipeListState.Success(
                    recipes = allRecipes.toList(),
                    hasMore = !isEndReached
                )
            } catch (_: Exception) {
                // Ignore pagination errors but leave the current data untouched
            } finally {
                isLoadingMore = false
            }
        }
    }

    fun retryLoadRecipes() {
        loadRecipes(
            refresh = true,
            categoryIds = activeCategoryIds,
            tagIds = activeTagIds
        )
    }

    fun loadRecipeDetail(slug: String) {
        viewModelScope.launch {
            _recipeDetailState.value = RecipeDetailState.Loading
            _favoriteState.value = null
            currentRecipeSlug = slug
            try {
                val recipe = repository.getRecipeBySlug(slug)
                _recipeDetailState.value = RecipeDetailState.Success(recipe)
                currentRecipeId = recipe.id
                fetchFavoriteStatus(recipe.id)
            } catch (e: Exception) {
                _recipeDetailState.value = RecipeDetailState.Error(
                    e.message ?: "Failed to load recipe details"
                )
                Logger.e("RecipeViewModel", "Failed to load recipe details", e)
            }
        }
    }

    fun searchRecipes(query: String) {
        _searchQuery.value = query
        loadRecipes(
            refresh = true,
            categoryIds = activeCategoryIds,
            tagIds = activeTagIds
        )
    }

    fun refresh() {
        loadRecipes(
            refresh = true,
            categoryIds = activeCategoryIds,
            tagIds = activeTagIds
        )
    }

    private fun updateRecipe(slug: String, updateRequest: RecipeDetail) {
        viewModelScope.launch {
            val currentState = _recipeDetailState.value
            if (currentState is RecipeDetailState.Success) {
                try {
                    val updatedRecipe = repository.updateRecipe(slug, updateRequest)
                    _recipeDetailState.value = RecipeDetailState.Success(updatedRecipe)
                } catch (e: Exception) {
                    _recipeDetailState.value = RecipeDetailState.Error(e.message ?: "Failed to update recipe")
                }
            }
        }
    }

    fun updateRecipeCategories(newCategories: List<RecipeCategory>) {
        val currentState = _recipeDetailState.value
        if (currentState is RecipeDetailState.Success) {
            val currentRecipe = currentState.recipe
            val updatedRecipe = currentRecipe.copy(
                recipeCategory = newCategories
            )
            updateRecipe(
                slug = currentState.recipe.slug,
                updateRequest = updatedRecipe
            )
        }
    }

    fun updateRecipeTags(newTags: List<RecipeTag>) {
        val currentState = _recipeDetailState.value
        if (currentState is RecipeDetailState.Success) {
            val currentRecipe = currentState.recipe
            val updatedRecipe = currentRecipe.copy(
                tags = newTags
            )
            updateRecipe(
                slug = currentState.recipe.slug,
                updateRequest = updatedRecipe
            )
        }
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

    fun loadFavoriteRecipes() {
        viewModelScope.launch {
            _favoriteRecipesState.value = FavoriteRecipesState.Loading
            try {
                val userId = ensureCurrentUserId()
                    ?: throw IllegalStateException("Unable to load user information")

                val response = userApi.getFavorites(userId)
                val favoriteIds = response.ratings
                    .filter { it.isFavorite != false }
                    .map { it.recipeId }
                    .distinct()

                if (favoriteIds.isEmpty()) {
                    _favoriteRecipesState.value = FavoriteRecipesState.Success(emptyList())
                    return@launch
                }

                val recipes = favoriteIds.mapNotNull { recipeId ->
                    runCatching { repository.getRecipeBySlug(recipeId) }
                        .getOrElse {
                            Logger.w(
                                "RecipeViewModel",
                                "Failed to fetch favorite recipe $recipeId: ${it.message}"
                            )
                            null
                        }?.toSummary()
                }

                _favoriteRecipesState.value = FavoriteRecipesState.Success(recipes)
            } catch (e: Exception) {
                Logger.e("RecipeViewModel", "Failed to load favorite recipes", e)
                _favoriteRecipesState.value = FavoriteRecipesState.Error(
                    e.message ?: "Failed to load favorite recipes"
                )
            }
        }
    }

    fun toggleFavorite() {
        if (_favoriteInProgress.value) return
        val slug = currentRecipeSlug ?: return

        viewModelScope.launch {
            val userId = ensureCurrentUserId() ?: return@launch
            val currentFavorite = _favoriteState.value ?: return@launch

            _favoriteInProgress.value = true
            try {
                if (currentFavorite) {
                    userApi.removeFavorite(userId, slug)
                } else {
                    userApi.addFavorite(userId, slug)
                }
                _favoriteState.value = !currentFavorite
            } catch (e: Exception) {
                Logger.e("RecipeViewModel", "Failed to toggle favorite", e)
            } finally {
                _favoriteInProgress.value = false
            }
        }
    }

    private fun fetchFavoriteStatus(recipeId: String) {
        viewModelScope.launch {
            try {
                val summary = userApi.getCurrentUserRatingForRecipe(recipeId)
                _favoriteState.value = summary.isFavorite ?: false
            } catch (e: Exception) {
                Logger.w("RecipeViewModel", "Failed to fetch favorite status: ${e.message}")
                _favoriteState.value = false
            }
        }
    }

    private suspend fun ensureCurrentUserId(): String? {
        currentUserId?.let { return it }
        return try {
            val user = userApi.getCurrentUser()
            currentUserId = user.id
            user.id
        } catch (e: Exception) {
            Logger.e("RecipeViewModel", "Unable to fetch current user", e)
            null
        }
    }
}

private fun RecipeDetail.toSummary(): RecipeSummary {
    return RecipeSummary(
        id = id,
        name = name,
        slug = slug,
        image = image,
        description = description,
        rating = rating,
        prepTime = prepTime,
        totalTime = totalTime,
        recipeYield = recipeYield,
        dateAdded = dateAdded,
        dateUpdated = dateUpdated,
        recipeCategory = recipeCategory,
        tags = tags
    )
}
