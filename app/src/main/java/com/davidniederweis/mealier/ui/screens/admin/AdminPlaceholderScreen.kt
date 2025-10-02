package com.davidniederweis.mealier.ui.screens.admin

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import com.davidniederweis.mealier.ui.components.layout.BottomNavBar

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AdminPlaceholderScreen(
    navController: NavHostController,
    isAdmin: Boolean
) {
    androidx.compose.material3.Scaffold(
        topBar = {
            androidx.compose.material3.TopAppBar(
                title = { androidx.compose.material3.Text("Admin Panel") }
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                isAdmin = isAdmin
            )
        }
    ) { paddingValues ->
        androidx.compose.foundation.layout.Box(
            modifier = androidx.compose.ui.Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = "Admin Panel - Coming Soon",
                style = androidx.compose.material3.MaterialTheme.typography.headlineMedium
            )
        }
    }
}
