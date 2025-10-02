package com.davidniederweis.mealier.ui.viewmodel.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.repository.AuthRepository
import com.davidniederweis.mealier.data.security.SecureDataStoreManager
import com.davidniederweis.mealier.data.repository.Result
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val tokenManager: SecureDataStoreManager
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
                val biometricEnabled = tokenManager.isBiometricEnabledOnce()
                _biometricEnabled.value = biometricEnabled

                if (biometricEnabled) {
                    _showBiometricPrompt.value = true
                } else {
                    loadCurrentUser()
                }
            } else {
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
                            tokenManager.setBiometricEnabled(true)
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
            tokenManager.setBiometricEnabled(false)
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
            tokenManager.setBiometricEnabled(enabled)
            _biometricEnabled.value = enabled
        }
    }
}
