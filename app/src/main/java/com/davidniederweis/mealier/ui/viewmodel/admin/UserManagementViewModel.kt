
package com.davidniederweis.mealier.ui.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.model.user.User
import com.davidniederweis.mealier.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class UserManagementState {
    object Idle : UserManagementState()
    object Loading : UserManagementState()
    data class Success(val users: List<User>) : UserManagementState()
    data class Error(val message: String) : UserManagementState()
}

class UserManagementViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _userManagementState = MutableStateFlow<UserManagementState>(UserManagementState.Idle)
    val userManagementState: StateFlow<UserManagementState> = _userManagementState.asStateFlow()

    private val _users = MutableStateFlow<List<User>>(emptyList())

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _sortAscending = MutableStateFlow(true)

    fun getUsers() {
        viewModelScope.launch {
            _userManagementState.value = UserManagementState.Loading
            try {
                val users = userRepository.getAllUsers()
                _users.value = users
                updateUserList()
            } catch (e: Exception) {
                _userManagementState.value = UserManagementState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }

    fun search(query: String) {
        _searchQuery.value = query
        updateUserList()
    }

    fun toggleSort() {
        _sortAscending.value = !_sortAscending.value
        updateUserList()
    }

    private fun updateUserList() {
        val filteredUsers = if (_searchQuery.value.isBlank()) {
            _users.value
        } else {
            _users.value.filter {
                it.username.contains(_searchQuery.value, ignoreCase = true) ||
                it.email.contains(_searchQuery.value, ignoreCase = true)
            }
        }

        val sortedUsers = if (_sortAscending.value) {
            filteredUsers.sortedBy { it.username }
        } else {
            filteredUsers.sortedByDescending { it.username }
        }

        _userManagementState.value = UserManagementState.Success(sortedUsers)
    }

    fun inviteUser(email: String) {
        viewModelScope.launch {
            try {
                userRepository.inviteUser(email)
                getUsers() // Refresh list
            } catch (e: Exception) {
                _userManagementState.value = UserManagementState.Error("Failed to invite user: ${e.message}")
            }
        }
    }

    fun deleteUser(userId: String) {
        viewModelScope.launch {
            try {
                userRepository.deleteUser(userId)
                getUsers() // Refresh list
            } catch (e: Exception) {
                _userManagementState.value = UserManagementState.Error("Failed to delete user: ${e.message}")
            }
        }
    }

    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(user)
                getUsers() // Refresh list
            } catch (e: Exception) {
                _userManagementState.value = UserManagementState.Error("Failed to update user: ${e.message}")
            }
        }
    }
}
