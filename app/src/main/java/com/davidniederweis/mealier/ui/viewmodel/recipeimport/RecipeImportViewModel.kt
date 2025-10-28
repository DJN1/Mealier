package com.davidniederweis.mealier.ui.viewmodel.recipeimport

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.repository.ImportRecipeFromUrlUseCase
import com.davidniederweis.mealier.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RecipeImportViewModel(private val importRecipeFromUrlUseCase: ImportRecipeFromUrlUseCase) : ViewModel() {

    private val _importState = MutableStateFlow<ImportState>(ImportState.Idle)
    val importState: StateFlow<ImportState> = _importState

    fun importRecipe(url: String) {
        viewModelScope.launch {
            _importState.value = ImportState.Loading
            when (val result = importRecipeFromUrlUseCase(url)) {
                is Result.Success -> {
                    _importState.value = ImportState.Success
                }
                is Result.Error -> {
                    _importState.value = ImportState.Error(result.message)
                }
                is Result.Loading -> {
                    // Already handled by setting state to Loading before the call
                }
            }
        }
    }
}

sealed class ImportState {
    object Idle : ImportState()
    object Loading : ImportState()
    object Success : ImportState()
    data class Error(val message: String) : ImportState()
}