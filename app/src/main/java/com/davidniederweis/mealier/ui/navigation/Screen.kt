package com.davidniederweis.mealier.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object AddRecipe : Screen("add_recipe?tab={tab}") {
        fun createRoute(tab: Int = 0) = "add_recipe?tab=$tab"
    }
    object RecipeDetail : Screen("recipe_detail/{slug}") {
        fun createRoute(slug: String) = "recipe_detail/$slug"
    }
    object EditRecipe : Screen("edit_recipe/{slug}") {
        fun createRoute(slug: String) = "edit_recipe/$slug"
    }
    object Admin : Screen("admin")
}
