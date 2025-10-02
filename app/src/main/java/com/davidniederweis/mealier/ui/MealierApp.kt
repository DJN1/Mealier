package com.davidniederweis.mealier.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.davidniederweis.mealier.ui.navigation.Screen
import com.davidniederweis.mealier.ui.navigation.NavGraph
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.auth.AuthViewModel

@Composable
fun MealierApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = appViewModel()
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()

    // Check login status on app start
//    LaunchedEffect(Unit) {
//        authViewModel.checkLoginStatus()
//    }checkLoginStatus

    // Determine start destination based on login state
    val startDestination = if (isLoggedIn) Screen.Home.route else Screen.Login.route

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavGraph(
            navController = navController,
            startDestination = startDestination
        )
    }
}