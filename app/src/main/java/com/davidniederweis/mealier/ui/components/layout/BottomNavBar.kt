package com.davidniederweis.mealier.ui.components.layout

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.davidniederweis.mealier.ui.navigation.Screen

@Composable
fun BottomNavBar(
    navController: NavController,
    isAdmin: Boolean
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        // Recipes (renamed from Journal)
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Restaurant,
                    contentDescription = "Recipes"
                )
            },
            label = { Text("Recipes") },
            selected = currentRoute == Screen.Home.route,
            onClick = {
                if (currentRoute != Screen.Home.route) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                }
            }
        )

        // Admin (only for admins)
        if (isAdmin) {
            NavigationBarItem(
                icon = {
                    Icon(
                        Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin"
                    )
                },
                label = { Text("Admin") },
                selected = currentRoute == Screen.Admin.route,
                onClick = {
                    if (currentRoute != Screen.Admin.route) {
                        navController.navigate(Screen.Admin.route)
                    }
                }
            )
        }

        // Profile (always show)
        NavigationBarItem(
            icon = {
                Icon(
                    Icons.Default.Person,
                    contentDescription = "Profile"
                )
            },
            label = { Text("Profile") },
            selected = currentRoute == Screen.Profile.route,
            onClick = {
                if (currentRoute != Screen.Profile.route) {
                    navController.navigate(Screen.Profile.route) {
                        popUpTo(Screen.Home.route)
                    }
                }
            }
        )
    }
}
