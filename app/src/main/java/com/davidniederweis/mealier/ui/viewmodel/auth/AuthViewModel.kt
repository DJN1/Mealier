package com.davidniederweis.mealier.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.repository.AuthRepository
import com.davidniederweis.mealier.data.security.SecureDataStoreManager
import com.davidniederweis.mealier.data.preferences.BiometricsPreferences
import com.davidniederweis.mealier.data.repository.Result
import com.davidniederweis.mealier.util.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: SecureDataStoreManager,
    private val biometricsPreferences: BiometricsPreferences
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _biometricEnabled = MutableStateFlow(false)
    val biometricEnabled: StateFlow<Boolean> = _biometricEnabled.asStateFlow()

    private val _showBiometricPrompt = MutableStateFlow(false)
    val showBiometricPrompt: StateFlow<Boolean> = _showBiometricPrompt.asStateFlow()

    init {
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        viewModelScope.launch {
            val loggedIn = authRepository.isLoggedIn()
            _isLoggedIn.value = loggedIn

            if (loggedIn) {
                Logger.i("AuthViewModel", "Token found, validating...")
                // Always try to validate the token by fetching current user
                // This will trigger automatic refresh if token is expired (401)
                loadCurrentUser()
            } else {
                Logger.i("AuthViewModel", "No token found")
                _authState.value = AuthState.Unauthenticated
            }
        }
    }

    fun onBiometricSuccess() {
        _showBiometricPrompt.value = false
        loadCurrentUser()
    }

    fun onBiometricError() {
        viewModelScope.launch {
            logout()
            _authState.value = AuthState.Error("Biometric authentication failed")
        }
    }

    fun skipBiometric() {
        _showBiometricPrompt.value = false
        loadCurrentUser()
    }

    private fun loadCurrentUser() {
        viewModelScope.launch {
            authRepository.getCurrentUser().collect { result ->
                _authState.value = when (result) {
                    is Result.Loading -> AuthState.Loading
                    is Result.Success -> {
                        _isLoggedIn.value = true
                        AuthState.Success(result.data)
                    }
                    is Result.Error -> {
                        _isLoggedIn.value = false
                        AuthState.Error(result.message)
                    }
                }
            }
        }
    }

    fun login(username: String, password: String, enableBiometric: Boolean = false) {
        viewModelScope.launch {
            authRepository.login(username, password).collect { result ->
                _authState.value = when (result) {
                    is Result.Loading -> AuthState.Loading
                    is Result.Success -> {
                        _isLoggedIn.value = true
                        if (enableBiometric) {
                            biometricsPreferences.setBiometricEnabled(true)
                            _biometricEnabled.value = true
                        }
                        AuthState.Success(result.data)
                    }
                    is Result.Error -> {
                        _isLoggedIn.value = false
                        AuthState.Error(result.message)
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            biometricsPreferences.setBiometricEnabled(false)
            _isLoggedIn.value = false
            _biometricEnabled.value = false
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun clearError() {
        if (_authState.value is AuthState.Error) {
            _authState.value = AuthState.Idle
        }
    }

    fun toggleBiometric(enabled: Boolean) {
        viewModelScope.launch {
            biometricsPreferences.setBiometricEnabled(enabled)
            _biometricEnabled.value = enabled
        }
    }
}
