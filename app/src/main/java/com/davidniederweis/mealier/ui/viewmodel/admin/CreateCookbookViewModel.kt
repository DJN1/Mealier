package com.davidniederweis.mealier.ui.viewmodel.admin

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.category.Category
import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.model.household.Household
import com.davidniederweis.mealier.data.model.tag.Tag
import com.davidniederweis.mealier.data.model.tool.Tool
import com.davidniederweis.mealier.data.models.CreateCookBook
import com.davidniederweis.mealier.data.repository.RecipeRepository
import com.davidniederweis.mealier.ui.screens.admin.FilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateCookbookViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags: StateFlow<List<Tag>> = _tags

    private val _ingredients = MutableStateFlow<List<Food>>(emptyList())
    val ingredients: StateFlow<List<Food>> = _ingredients

    private val _tools = MutableStateFlow<List<Tool>>(emptyList())
    val tools: StateFlow<List<Tool>> = _tools

    private val _households = MutableStateFlow<List<Household>>(emptyList())
    val households: StateFlow<List<Household>> = _households

    private val _cookbookSaved = MutableStateFlow(false)
    val cookbookSaved: StateFlow<Boolean> = _cookbookSaved

    fun loadInitialData() {
        viewModelScope.launch {
            try {
                Log.d("CreateCookbookViewModel", "Loading initial data...")
                _categories.value = recipeRepository.getCategories()
                _tags.value = recipeRepository.getTags()
                _ingredients.value = recipeRepository.getFoods()
                _tools.value = recipeRepository.getTools()
                _households.value = recipeRepository.getHouseholds()
                Log.d("CreateCookbookViewModel", "Initial data loaded successfully")
            } catch (e: Exception) {
                Log.e("CreateCookbookViewModel", "Failed to load initial data", e)
            }
        }
    }

    private fun createQueryFilterString(filters: List<FilterState>): String {
        return filters.joinToString(" AND ") { filter ->
            val category = when (filter.selectedCategory) {
                "Categories" -> "recipeCategory.id"
                "Tags" -> "tags.id"
                "Ingredients" -> "ingredients.id"
                "Tools" -> "tools.id"
                "Households" -> "household.id"
                "Date Created" -> "dateCreated"
                "Date Updated" -> "dateUpdated"
                else -> ""
            }
            val operator = when (filter.selectedOperator) {
                "is one of" -> "IN"
                "is not one of" -> "NOT IN"
                "contains all of" -> "CONTAINS ALL"
                else -> ""
            }
            val value = if (filter.selectedCategory.startsWith("Date")) {
                "'${filter.selectedValue.first}'"
            } else {
                "['${filter.selectedValue.first}']"
            }
            "$category $operator $value"
        }
    }

    fun saveCookbook(
        cookbookName: String,
        description: String,
        isPublic: Boolean,
        filters: List<FilterState>
    ) {
        viewModelScope.launch {
            val queryFilterString = createQueryFilterString(filters)
            val cookbook = CreateCookBook(
                name = cookbookName,
                description = description,
                public = isPublic,
                queryFilterString = queryFilterString
            )
            try {
                recipeRepository.createCookbook(cookbook)
                _cookbookSaved.value = true
                Log.d("CreateCookbookViewModel", "Cookbook saved successfully")
            } catch (e: Exception) {
                Log.e("CreateCookbookViewModel", "Failed to save cookbook", e)
            }
        }
    }
}
