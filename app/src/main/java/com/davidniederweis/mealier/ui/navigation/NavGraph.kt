package com.davidniederweis.mealier.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.davidniederweis.mealier.ui.screens.admin.AdminPlaceholderScreen
import com.davidniederweis.mealier.ui.screens.auth.LoginScreen
import com.davidniederweis.mealier.ui.screens.home.HomeScreen
import com.davidniederweis.mealier.ui.screens.profile.ProfileScreen
import com.davidniederweis.mealier.ui.screens.recipe.AddRecipeScreen
import com.davidniederweis.mealier.ui.screens.recipe.RecipeDetailScreen
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.profile.ProfileState
import com.davidniederweis.mealier.ui.viewmodel.profile.ProfileViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String
) {
    // Get profile view model to check admin status
    val profileViewModel: ProfileViewModel = appViewModel()
    val profileState by profileViewModel.profileState.collectAsState()

    // Extract admin status from profile state
    val isAdmin = when (val state = profileState) {
        is ProfileState.Success -> state.user.admin
        else -> false
    }

    // Load profile when nav graph is initialized
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Screens
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    // Reload profile after login
                    profileViewModel.loadProfile()
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
            )
        }

        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onRecipeClick = { slug ->
                    navController.navigate(Screen.RecipeDetail.createRoute(slug))
                },
                onAddRecipeClick = {
                    navController.navigate(Screen.AddRecipe.route)
                },
                navController = navController,
                isAdmin = isAdmin
            )
        }

        // Recipe Detail
        composable(
            route = Screen.RecipeDetail.route,
            arguments = listOf(
                navArgument("slug") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val slug = backStackEntry.arguments?.getString("slug") ?: return@composable
            RecipeDetailScreen(
                slug = slug,
                onNavigateBack = {
                    navController.popBackStack()
                },
                navController = navController,
                isAdmin = isAdmin
            )
        }

        // Add Recipe Screen
        composable(
            route = Screen.AddRecipe.route,
            arguments = listOf(
                navArgument("tab") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val initialTab = backStackEntry.arguments?.getInt("tab") ?: 0
            AddRecipeScreen(
                initialTab = initialTab,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRecipeCreated = { slug ->
                    // Navigate to the newly created recipe detail
                    navController.navigate(Screen.RecipeDetail.createRoute(slug)) {
                        popUpTo(Screen.AddRecipe.route) { inclusive = true }
                    }
                },
                navController = navController,
                isAdmin = isAdmin
            )
        }

        // Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                navController = navController,
                isAdmin = isAdmin
            )
        }

        // Admin Screen (placeholder for now)
        composable(Screen.Admin.route) {
            AdminPlaceholderScreen(
                navController = navController,
                isAdmin = isAdmin
            )
        }
    }
}
