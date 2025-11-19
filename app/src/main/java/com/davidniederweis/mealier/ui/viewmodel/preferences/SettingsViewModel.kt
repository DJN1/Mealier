package com.davidniederweis.mealier.ui.viewmodel.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.preferences.AppPreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val appPreferences: AppPreferences
) : ViewModel() {

    val keepScreenOn: StateFlow<Boolean> = appPreferences.keepScreenOn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun setKeepScreenOn(enabled: Boolean) {
        viewModelScope.launch {
            appPreferences.setKeepScreenOn(enabled)
        }
    }
}
