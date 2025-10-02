package com.davidniederweis.mealier.ui.viewmodel.profile

import com.davidniederweis.mealier.data.model.user.UserProfile

sealed class ProfileState {
    data object Idle : ProfileState()
    data object Loading : ProfileState()
    data class Success(val user: UserProfile) : ProfileState()
    data class Error(val message: String) : ProfileState()
}
