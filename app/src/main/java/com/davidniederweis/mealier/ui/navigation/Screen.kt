package com.davidniederweis.mealier.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Favorites : Screen("favorites")
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
    object DataManagement : Screen("data_management")
    object FoodManagement : Screen("food_management")
    object GroupManagement : Screen("group_management")
    object CookbookManagement : Screen("cookbook_management")
    object CreateCookbook : Screen("create_cookbook")
    object EditCookbook : Screen("edit_cookbook/{id}") {
        fun createRoute(id: String) = "edit_cookbook/$id"
    }
    object UnitManagement : Screen("unit_management")
    object TagManagement : Screen("tag_management")
    object UserManagement : Screen("user_management")
    object Household : Screen("household")
    object HouseholdSettings : Screen("household_settings")
    object Members : Screen("members")
    object Webhooks : Screen("webhooks")
    object Notifiers : Screen("notifiers")
}
