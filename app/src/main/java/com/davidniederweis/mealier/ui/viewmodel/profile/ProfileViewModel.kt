package com.davidniederweis.mealier.ui.viewmodel.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.api.UserApi
import com.davidniederweis.mealier.data.model.user.UserProfile
import com.davidniederweis.mealier.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val userApi: UserApi
) : ViewModel() {

    private val _profileState = MutableStateFlow<ProfileState>(ProfileState.Idle)
    val profileState: StateFlow<ProfileState> = _profileState.asStateFlow()

    fun loadProfile() {
        viewModelScope.launch {
            _profileState.value = ProfileState.Loading
            try {
                Logger.d("ProfileViewModel", "Loading user profile")
                val user = userApi.getCurrentUser()
                Logger.i("ProfileViewModel", "Profile loaded successfully: ${user.username}")
                _profileState.value = ProfileState.Success(user)
            } catch (e: Exception) {
                Logger.e("ProfileViewModel", "Failed to load profile", e)
                _profileState.value = ProfileState.Error(e.message ?: "Failed to load profile")
            }
        }
    }
}
