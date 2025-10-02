package com.davidniederweis.mealier.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davidniederweis.mealier.MealierApplication

@Composable
inline fun <reified T : androidx.lifecycle.ViewModel> appViewModel(): T {
    val application = LocalContext.current.applicationContext as MealierApplication
    return viewModel(factory = application.viewModelFactory)
}