package com.davidniederweis.mealier.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.davidniederweis.mealier.ui.components.addrecipe.AddRecipeOptionsDialog
import com.davidniederweis.mealier.ui.components.layout.BottomNavBar
import com.davidniederweis.mealier.ui.components.general.EmptyState
import com.davidniederweis.mealier.ui.components.general.ErrorMessage
import com.davidniederweis.mealier.ui.components.general.LoadingBox
import com.davidniederweis.mealier.ui.components.recipe.RecipeCard
import com.davidniederweis.mealier.ui.components.layout.RecipeSearchBar
import com.davidniederweis.mealier.ui.components.layout.adaptiveGridCells
import com.davidniederweis.mealier.ui.navigation.Screen
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeListState
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onRecipeClick: (String) -> Unit,
    onAddRecipeClick: () -> Unit,
    navController: NavController,
    isAdmin: Boolean,
    viewModel: RecipeViewModel = appViewModel()
) {
    val recipeListState by viewModel.recipeListState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val listState = rememberLazyGridState()

    var isRefreshing by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    // Handle refresh state
    LaunchedEffect(recipeListState) {
        if (recipeListState !is RecipeListState.Loading) {
            isRefreshing = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recipes") },
                actions = {
                    IconButton(onClick = { /* TODO: Open filter */ }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filter"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                isAdmin = isAdmin
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Recipe"
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            RecipeSearchBar(
                query = searchQuery,
                onQueryChange = { query ->
                    viewModel.searchRecipes(query)
                },
                onSearch = {
                    viewModel.searchRecipes(searchQuery)
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Content with pull-to-refresh
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    viewModel.loadRecipes(refresh = true)
                },
                modifier = Modifier.fillMaxSize()
            ) {
                when (val state = recipeListState) {
                    is RecipeListState.Loading -> {
                        if (!isRefreshing) {
                            LoadingBox(modifier = Modifier.fillMaxSize())
                        }
                    }

                    is RecipeListState.Success -> {
                        if (state.recipes.isEmpty()) {
                            EmptyState(
                                message = if (searchQuery.isNotEmpty()) {
                                    "No recipes found for \"$searchQuery\""
                                } else {
                                    "No recipes yet"
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Restaurant,
                                        contentDescription = null,
                                        modifier = Modifier.size(64.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            )
                        } else {
                            LazyVerticalGrid(
                                columns = adaptiveGridCells(),
                                state = listState,
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items(
                                    items = state.recipes,
                                    key = { it.id }
                                ) { recipe ->
                                    RecipeCard(
                                        recipe = recipe,
                                        onClick = { onRecipeClick(recipe.slug) }
                                    )
                                }

                                // Load more indicator at the end
                                if (state.recipes.size >= 50) {
                                    item(span = { GridItemSpan(maxLineSpan) }) {
                                        LaunchedEffect(Unit) {
                                            viewModel.loadMoreRecipes()
                                        }

                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(32.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    is RecipeListState.Error -> {
                        ErrorMessage(
                            message = state.message,
                            onRetry = { viewModel.retryLoadRecipes() },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    is RecipeListState.Idle -> {
                        if (!isRefreshing) {
                            LoadingBox(modifier = Modifier.fillMaxSize())
                        }
                    }
                }
            }
        }
    }

    // Add Recipe Options Dialog
    if (showAddDialog) {
        AddRecipeOptionsDialog(
            onDismiss = { showAddDialog = false },
            onManualClick = {
                showAddDialog = false
                navController.navigate(Screen.AddRecipe.createRoute(tab = 0))
            },
            onUrlClick = {
                showAddDialog = false
                navController.navigate(Screen.AddRecipe.createRoute(tab = 1))
            }
        )
    }
}
