package com.davidniederweis.mealier.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.category.Category
import com.davidniederweis.mealier.data.model.cookbook.Cookbook
import com.davidniederweis.mealier.data.model.cookbook.QueryFilter
import com.davidniederweis.mealier.data.model.cookbook.QueryFilterPart
import com.davidniederweis.mealier.data.model.cookbook.UpdateCookbook
import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.model.household.Household
import com.davidniederweis.mealier.data.model.tag.Tag
import com.davidniederweis.mealier.data.model.tool.Tool
import com.davidniederweis.mealier.data.repository.RecipeRepository
import com.davidniederweis.mealier.ui.screens.admin.FilterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EditCookbookViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _cookbook = MutableStateFlow<Cookbook?>(null)
    val cookbook: StateFlow<Cookbook?> = _cookbook

    private val _cookbookSaved = MutableStateFlow(false)
    val cookbookSaved: StateFlow<Boolean> = _cookbookSaved

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

    // Add this new state flow for filters
    private val _filters = MutableStateFlow<List<FilterState>>(emptyList())
    val filters: StateFlow<List<FilterState>> = _filters

    fun loadCookbook(id: String) {
        viewModelScope.launch {
            try {
                val fetchedCookbook = recipeRepository.getCookbook(id)
                _cookbook.value = fetchedCookbook
                val filterString = fetchedCookbook.queryFilterString
                if (filterString.isNotBlank()) {
                    println("FilterString: $filterString")
                    _filters.value = filterString.split(" AND ").mapNotNull { partString ->
                        println("PartString: $partString")
                        val regex = """(.+) (IN|NOT IN|CONTAINS ALL) \["(.+)"]""".toRegex()
                        regex.find(partString)?.let { matchResult ->
                            val (attributeName, relationalOperator, value) = matchResult.destructured
                            println("  AttributeName: $attributeName, Operator: $relationalOperator, Value: $value")
                            FilterState(
                                selectedCategory = when (attributeName) {
                                    "recipe_category.id" -> "Categories"
                                    "tags.id" -> "Tags"
                                    "ingredients.id" -> "Ingredients"
                                    "tools.id" -> "Tools"
                                    "household.id" -> "Households"
                                    "dateCreated" -> "Date Created"
                                    "dateUpdated" -> "Date Updated"
                                    else -> ""
                                },
                                selectedOperator = when (relationalOperator) {
                                    "IN" -> "is one of"
                                    "NOT IN" -> "is not one of"
                                    "CONTAINS ALL" -> "contains all of"
                                    else -> ""
                                },
                                selectedValue = value to value
                            ).also { filterState ->
                                println("  Created FilterState: category=${filterState.selectedCategory}, operator=${filterState.selectedOperator}, selectedValue=${filterState.selectedValue.first} to ${filterState.selectedValue.second}")
                            }
                        } ?: run {
                            println("  Regex did not match for partString: $partString")
                            null
                        }
                    }
                } else {
                    println("Cookbook queryFilterString is null or blank. Setting filters to emptyList.")
                    _filters.value = emptyList()
                }
            } catch (e: Exception) {
                println("Error loading cookbook or parsing filterString: ${e.message}")
                // Handle error
            }
        }
    }

    fun loadInitialData() {
        viewModelScope.launch {
            try {
                _categories.value = recipeRepository.getCategories()
                _tags.value = recipeRepository.getTags()
                _ingredients.value = recipeRepository.getFoods()
                _tools.value = recipeRepository.getTools()
                _households.value = recipeRepository.getHouseholds()
            } catch (_: Exception) {
                // Handle error
            }
        }
    }

    private fun createQueryFilterString(filters: List<FilterState>): String {
        return filters.joinToString(" AND ") { filter ->
            val category = when (filter.selectedCategory) {
                "Categories" -> "recipe_category.id"
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
                "[\"${filter.selectedValue.first}\"]"
            }
            "$category $operator $value"
        }
    }

    fun updateCookbook(
        cookbookId: String,
        cookbookName: String,
        description: String,
        isPublic: Boolean,
        filters: List<FilterState>
    ) {
        viewModelScope.launch {
            val queryFilterString = createQueryFilterString(filters)
            val queryFilter = QueryFilter(
                parts = filters.map {
                    QueryFilterPart(
                        attributeName = when (it.selectedCategory) {
                            "Categories" -> "recipe_category.id"
                            "Tags" -> "tags.id"
                            "Ingredients" -> "ingredients.id"
                            "Tools" -> "tools.id"
                            "Households" -> "household.id"
                            "Date Created" -> "dateCreated"
                            "Date Updated" -> "dateUpdated"
                            else -> ""
                        },
                        relationalOperator = when (it.selectedOperator) {
                            "is one of" -> "IN"
                            "is not one of" -> "NOT IN"
                            "contains all of" -> "CONTAINS ALL"
                            else -> ""
                        },
                        value = listOf(it.selectedValue.first)
                    )
                }
            )

            val currentCookbook = _cookbook.value
            if (currentCookbook != null) {
                val cookbook = UpdateCookbook(
                    name = cookbookName,
                    description = description,
                    public = isPublic,
                    queryFilterString = queryFilterString,
                    slug = currentCookbook.slug,
                    position = currentCookbook.position,
                    groupId = currentCookbook.groupId,
                    householdId = currentCookbook.householdId,
                    id = cookbookId,
                    queryFilter = queryFilter,
                    household = currentCookbook.household
                )
                try {
                    recipeRepository.updateCookbook(cookbookId, cookbook)
                    _cookbookSaved.value = true
                } catch (_: Exception) {
                    // Handle error
                }
            }

        }
    }
}