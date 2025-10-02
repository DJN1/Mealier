package com.davidniederweis.mealier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davidniederweis.mealier.ui.MealierApp
import com.davidniederweis.mealier.ui.theme.MealierTheme
import com.davidniederweis.mealier.ui.viewmodel.preferences.ThemeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val application = application as MealierApplication

        setContent {
            val themeViewModel: ThemeViewModel = viewModel(
                factory = application.viewModelFactory
            )
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            MealierTheme(
                darkTheme = isDarkMode,
                dynamicColor = true
            ) {
                MealierApp()
            }
        }
    }
}