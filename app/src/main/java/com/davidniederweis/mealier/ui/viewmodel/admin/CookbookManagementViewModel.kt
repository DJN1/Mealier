package com.davidniederweis.mealier.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.cookbook.Cookbook
import com.davidniederweis.mealier.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CookbookManagementViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _cookbooks = MutableStateFlow<List<Cookbook>>(emptyList())
    val cookbooks: StateFlow<List<Cookbook>> = _cookbooks

    fun getCookbooks() {
        viewModelScope.launch {
            try {
                _cookbooks.value = recipeRepository.getCookbooks()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
