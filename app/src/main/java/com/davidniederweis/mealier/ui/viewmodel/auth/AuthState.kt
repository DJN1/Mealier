package com.davidniederweis.mealier.ui.viewmodel.auth

import com.davidniederweis.mealier.data.model.user.UserProfile

sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Success(val user: UserProfile) : AuthState()
    data class Error(val message: String) : AuthState()
    data object Unauthenticated : AuthState()
}

