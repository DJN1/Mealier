package com.davidniederweis.mealier.ui.viewmodel.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.preferences.BiometricsPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BiometricsViewModel(
    private val biometricsPreferences: BiometricsPreferences
) : ViewModel() {

    val biometricEnabled: StateFlow<Boolean> = biometricsPreferences.biometricEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = true
        )

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            biometricsPreferences.setBiometricEnabled(enabled)
        }
    }
}
