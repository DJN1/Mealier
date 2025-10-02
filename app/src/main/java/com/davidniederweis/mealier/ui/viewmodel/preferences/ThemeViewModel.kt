package com.davidniederweis.mealier.ui.viewmodel.preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.preferences.ThemePreferences
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ThemeViewModel(
    private val themePreferences: ThemePreferences
) : ViewModel() {

    val isDarkMode: StateFlow<Boolean> = themePreferences.isDarkMode
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    fun toggleDarkMode() {
        viewModelScope.launch {
            themePreferences.toggleDarkMode()
        }
    }

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            themePreferences.setDarkMode(enabled)
        }
    }
}