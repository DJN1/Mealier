package com.davidniederweis.mealier.ui.viewmodel.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidniederweis.mealier.data.preferences.ServerPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServerViewModel(
    private val serverPreferences: ServerPreferences
) : ViewModel() {

    private val _serverUrl = MutableStateFlow("")
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    init {
        viewModelScope.launch {
            serverPreferences.serverUrl.collect { url ->
                _serverUrl.value = url
            }
        }
    }

    fun setServerUrl(url: String) {
        viewModelScope.launch {
            serverPreferences.setServerUrl(url)
        }
    }

}
