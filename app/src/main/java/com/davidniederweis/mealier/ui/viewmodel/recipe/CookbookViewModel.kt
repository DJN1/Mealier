package com.davidniederweis.mealier.ui.viewmodel.recipe

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.cookbook.Cookbook
import com.davidniederweis.mealier.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CookbookViewModel(
    private val repository: RecipeRepository
) : ViewModel() {

    private val _cookbooks = MutableStateFlow<List<Cookbook>>(emptyList())
    val cookbooks: StateFlow<List<Cookbook>> = _cookbooks.asStateFlow()

    fun loadCookbooks() {
        viewModelScope.launch {
            try {
                _cookbooks.value = repository.getCookbooks()
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
