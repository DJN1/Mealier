package com.davidniederweis.mealier.ui.screens.recipe

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.davidniederweis.mealier.BuildConfig
import com.davidniederweis.mealier.ui.components.layout.BottomNavBar
import com.davidniederweis.mealier.ui.components.general.ErrorMessage
import com.davidniederweis.mealier.ui.components.general.LoadingBox
import com.davidniederweis.mealier.ui.components.recipe.RecipeDetailContent
import com.davidniederweis.mealier.ui.navigation.Screen
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeDetailState
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeDetailScreen(
    slug: String,
    onNavigateBack: () -> Unit,
    navController: NavController,
    isAdmin: Boolean,
    viewModel: RecipeViewModel = appViewModel()
) {
    val recipeDetailState by viewModel.recipeDetailState.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current

    LaunchedEffect(slug) {
        viewModel.loadRecipeDetail(slug)
    }

    // Function to share recipe
    fun shareRecipe(recipeSlug: String, recipeName: String?) {
        val recipeUrl = "${BuildConfig.BASE_URL}/recipe/$recipeSlug"
        val shareText = if (recipeName != null) {
            "Check out this recipe: $recipeName\n$recipeUrl"
        } else {
            "Check out this recipe:\n$recipeUrl"
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, shareText)
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, "Share recipe")
        context.startActivity(shareIntent)
    }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    when (val state = recipeDetailState) {
                        is RecipeDetailState.Success -> {
                            Text(state.recipe.name ?: "Recipe")
                        }
                        else -> Text("Recipe")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    // Only show actions when recipe is loaded
                    if (recipeDetailState is RecipeDetailState.Success) {
                        IconButton(
                            onClick = {
                                val recipe = (recipeDetailState as RecipeDetailState.Success).recipe
                                shareRecipe(slug, recipe.name)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share"
                            )
                        }
                        IconButton(onClick = { /* TODO: Favorite */ }) {
                            Icon(
                                imageVector = Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite"
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                isAdmin = isAdmin
            )
        },
        floatingActionButton = {
            // Show edit FAB only if admin and recipe is loaded
            if (isAdmin && recipeDetailState is RecipeDetailState.Success) {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(Screen.EditRecipe.createRoute(slug))
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Recipe"
                    )
                }
            }
        }
    ) { paddingValues ->
        when (val state = recipeDetailState) {
            is RecipeDetailState.Loading -> {
                LoadingBox(modifier = Modifier.fillMaxSize())
            }

            is RecipeDetailState.Success -> {
                RecipeDetailContent(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    recipe = state.recipe
                )
            }

            is RecipeDetailState.Error -> {
                ErrorMessage(
                    message = state.message,
                    onRetry = { viewModel.loadRecipeDetail(slug) },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is RecipeDetailState.Idle -> {
                LoadingBox(modifier = Modifier.fillMaxSize())
            }
        }
    }
}
