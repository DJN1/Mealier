package com.davidniederweis.mealier.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.unit.CreateUnitRequest
import com.davidniederweis.mealier.data.model.unit.RecipeUnit
import com.davidniederweis.mealier.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UnitManagementState {
    object Idle : UnitManagementState()
    object Loading : UnitManagementState()
    data class Success(val units: List<RecipeUnit>) : UnitManagementState()
    data class Error(val message: String) : UnitManagementState()
}

class UnitManagementViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _unitManagementState = MutableStateFlow<UnitManagementState>(UnitManagementState.Idle)
    val unitManagementState: StateFlow<UnitManagementState> = _unitManagementState.asStateFlow()

    private val _units = MutableStateFlow<List<RecipeUnit>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortAscending = MutableStateFlow(true)
    val sortAscending: StateFlow<Boolean> = _sortAscending.asStateFlow()

    fun getUnits() {
        viewModelScope.launch {
            _unitManagementState.value = UnitManagementState.Loading
            try {
                val units = recipeRepository.getUnits()
                _units.value = units
                updateUnitList()
            } catch (e: Exception) {
                _unitManagementState.value = UnitManagementState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        updateUnitList()
    }

    fun toggleSort() {
        _sortAscending.value = !_sortAscending.value
        updateUnitList()
    }

    private fun updateUnitList() {
        val filteredUnits = if (_searchQuery.value.isBlank()) {
            _units.value
        } else {
            _units.value.filter { it.name.contains(_searchQuery.value, ignoreCase = true) }
        }

        val sortedUnits = if (_sortAscending.value) {
            filteredUnits.sortedBy { it.name }
        } else {
            filteredUnits.sortedByDescending { it.name }
        }

        _unitManagementState.value = UnitManagementState.Success(sortedUnits)
    }

    fun createUnit(name: String, namePlural: String, abbreviation: String, abbreviationPlural: String) {
        viewModelScope.launch {
            try {
                val request = CreateUnitRequest(name, namePlural, abbreviation, abbreviationPlural)
                recipeRepository.createUnit(request)
                getUnits() // Refresh list
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    fun updateUnit(id: String, name: String, namePlural: String, abbreviation: String, abbreviationPlural: String) {
        viewModelScope.launch {
            try {
                val request = CreateUnitRequest(name, namePlural, abbreviation, abbreviationPlural)
                recipeRepository.updateUnit(id, request)
                getUnits() // Refresh list
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    fun deleteUnit(id: String) {
        viewModelScope.launch {
            try {
                recipeRepository.deleteUnit(id)
                getUnits() // Refresh list
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }
}