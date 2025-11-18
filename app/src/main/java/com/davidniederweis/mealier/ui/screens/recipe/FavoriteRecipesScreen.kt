package com.davidniederweis.mealier.ui.screens.recipe

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.davidniederweis.mealier.ui.components.general.EmptyState
import com.davidniederweis.mealier.ui.components.general.ErrorMessage
import com.davidniederweis.mealier.ui.components.general.LoadingBox
import com.davidniederweis.mealier.ui.components.layout.BottomNavBar
import com.davidniederweis.mealier.ui.components.layout.adaptiveGridCells
import com.davidniederweis.mealier.ui.components.recipe.RecipeCard
import com.davidniederweis.mealier.ui.navigation.Screen
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.FavoriteRecipesState
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FavoriteRecipesScreen(
    navController: NavController,
    isAdmin: Boolean,
    viewModel: RecipeViewModel = appViewModel()
) {
    val favoritesState by viewModel.favoriteRecipesState.collectAsState()
    val baseUrl by viewModel.baseUrl.collectAsState()
    var isRefreshing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadFavoriteRecipes()
    }

    LaunchedEffect(favoritesState) {
        if (favoritesState !is FavoriteRecipesState.Loading) {
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorite Recipes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavBar(navController = navController, isAdmin = isAdmin)
        }
    ) { paddingValues ->
        when (val state = favoritesState) {
            FavoriteRecipesState.Idle, FavoriteRecipesState.Loading -> {
                LoadingBox(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is FavoriteRecipesState.Error -> {
                ErrorMessage(
                    message = state.message,
                    onRetry = {
                        viewModel.loadFavoriteRecipes()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is FavoriteRecipesState.Success -> {
                PullToRefreshBox(
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        isRefreshing = true
                        viewModel.loadFavoriteRecipes()
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    val recipes = state.recipes
                    if (recipes.isEmpty()) {
                        EmptyState(
                            message = "No favorite recipes yet",
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        val listState = rememberLazyGridState()
                        LazyVerticalGrid(
                            columns = adaptiveGridCells(),
                            state = listState,
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = recipes,
                                key = { it.id }
                            ) { recipe ->
                                RecipeCard(
                                    recipe = recipe,
                                    onClick = {
                                        navController.navigate(Screen.RecipeDetail.createRoute(recipe.slug))
                                    },
                                    baseUrl = baseUrl
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
