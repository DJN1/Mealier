package com.davidniederweis.mealier.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.tag.CreateTagRequest
import com.davidniederweis.mealier.data.model.tag.Tag
import com.davidniederweis.mealier.data.repository.RecipeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class TagManagementState {
    object Idle : TagManagementState()
    object Loading : TagManagementState()
    data class Success(val tags: List<Tag>) : TagManagementState()
    data class Error(val message: String) : TagManagementState()
}

class TagManagementViewModel(
    private val recipeRepository: RecipeRepository
) : ViewModel() {

    private val _tagManagementState = MutableStateFlow<TagManagementState>(TagManagementState.Idle)
    val tagManagementState: StateFlow<TagManagementState> = _tagManagementState.asStateFlow()

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortAscending = MutableStateFlow(true)
    val sortAscending: StateFlow<Boolean> = _sortAscending.asStateFlow()

    fun getTags() {
        viewModelScope.launch {
            _tagManagementState.value = TagManagementState.Loading
            try {
                val tags = recipeRepository.getTags()
                _tags.value = tags
                updateTagList()
            } catch (e: Exception) {
                _tagManagementState.value = TagManagementState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        updateTagList()
    }

    fun toggleSort() {
        _sortAscending.value = !_sortAscending.value
        updateTagList()
    }

    private fun updateTagList() {
        val filteredTags = if (_searchQuery.value.isBlank()) {
            _tags.value
        } else {
            _tags.value.filter { it.name.contains(_searchQuery.value, ignoreCase = true) }
        }

        val sortedTags = if (_sortAscending.value) {
            filteredTags.sortedBy { it.name }
        } else {
            filteredTags.sortedByDescending { it.name }
        }

        _tagManagementState.value = TagManagementState.Success(sortedTags)
    }

    fun createTag(name: String) {
        viewModelScope.launch {
            try {
                val request = CreateTagRequest(name = name)
                recipeRepository.createTag(request)
                getTags() // Refresh list
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    fun updateTag(id: String, name: String) {
        viewModelScope.launch {
            try {
                val updatedTag = Tag(id = id, name = name)
                recipeRepository.updateTag(id, updatedTag)
                getTags() // Refresh list
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }

    fun deleteTag(id: String) {
        viewModelScope.launch {
            try {
                recipeRepository.deleteTag(id)
                getTags() // Refresh list
            } catch (e: Exception) {
                // TODO: Handle error
            }
        }
    }
}