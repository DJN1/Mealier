package com.davidniederweis.mealier.ui.screens.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.davidniederweis.mealier.data.model.cookbook.Cookbook
import com.davidniederweis.mealier.ui.components.addrecipe.AddRecipeOptionsDialog
import com.davidniederweis.mealier.ui.components.general.EmptyState
import com.davidniederweis.mealier.ui.components.general.ErrorMessage
import com.davidniederweis.mealier.ui.components.general.LoadingBox
import com.davidniederweis.mealier.ui.components.layout.BottomNavBar
import com.davidniederweis.mealier.ui.components.layout.RecipeSearchBar
import com.davidniederweis.mealier.ui.components.layout.adaptiveGridCells
import com.davidniederweis.mealier.ui.components.recipe.FilterDialog
import com.davidniederweis.mealier.ui.components.recipe.RecipeCard
import com.davidniederweis.mealier.ui.navigation.Screen
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeListState
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    onRecipeClick: (String) -> Unit,
    navController: NavController,
    isAdmin: Boolean,
    viewModel: RecipeViewModel = appViewModel()
) {
    val recipeListState by viewModel.recipeListState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val baseUrl by viewModel.baseUrl.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val tags by viewModel.allTags.collectAsState()
    val listState = rememberLazyGridState()

    var isRefreshing by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedCookbook by remember { mutableStateOf<Cookbook?>(null) }
    var selectedCategoryIds by remember { mutableStateOf(listOf<String>()) }
    var selectedTagIds by remember { mutableStateOf(listOf<String>()) }

    // Handle refresh state
    LaunchedEffect(recipeListState) {
        if (recipeListState !is RecipeListState.Loading) {
            isRefreshing = false
        }
    }

    val pagerState = rememberPagerState(pageCount = { 2 })
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.clearRecipes()
        if (pagerState.currentPage == 0) {
            viewModel.loadRecipes(categoryIds = selectedCategoryIds, tagIds = selectedTagIds)
        } else {
            selectedCookbook?.let {
                viewModel.loadRecipesByCookbook(it.slug)
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            categories = categories,
            tags = tags,
            initialSelectedCategoryIds = selectedCategoryIds,
            initialSelectedTagIds = selectedTagIds,
            onDismiss = { showFilterDialog = false },
            onApply = { newSelectedCategories, newSelectedTags ->
                selectedCategoryIds = newSelectedCategories
                selectedTagIds = newSelectedTags
                viewModel.loadRecipes(refresh = true, categoryIds = selectedCategoryIds, tagIds = selectedTagIds)
                showFilterDialog = false
            }
        )
    }

    androidx.compose.material3.Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .statusBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recipes",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    if (pagerState.currentPage == 0) {
                        IconButton(onClick = { showFilterDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = "Filter"
                            )
                        }
                    } else {
                        // Add a spacer to keep the title in the same place
                        Spacer(modifier = Modifier.height(48.dp)) // Same width as IconButton
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 8.dp, bottom = 12.dp)
                        .height(56.dp) // Set a fixed height for the search/cookbook area
                ) {
                    if (pagerState.currentPage == 0) {
                        RecipeSearchBar(
                            query = searchQuery,
                            onQueryChange = { query ->
                                viewModel.searchRecipes(query)
                            },
                            onSearch = {
                                viewModel.searchRecipes(searchQuery)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        CookbookDropdown(
                            selectedCookbook = selectedCookbook,
                            onCookbookSelected = { 
                                selectedCookbook = it
                                viewModel.loadRecipesByCookbook(it.slug)
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
                TabRow(selectedTabIndex = pagerState.currentPage) {
                    Tab(
                        selected = pagerState.currentPage == 0,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(0) } },
                        text = { Text("Search") }
                    )
                    Tab(
                        selected = pagerState.currentPage == 1,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(1) } },
                        text = { Text("Cookbooks") }
                    )
                }
            }
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
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { page ->
            PullToRefreshBox(
                isRefreshing = isRefreshing,
                onRefresh = {
                    isRefreshing = true
                    if (page == 0) {
                        viewModel.loadRecipes(refresh = true, categoryIds = selectedCategoryIds, tagIds = selectedTagIds)
                    } else {
                        selectedCookbook?.let {
                            viewModel.loadRecipesByCookbook(it.slug)
                        }
                    }
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
                                message = if (searchQuery.isNotEmpty() && page == 0) {
                                    "No recipes found for \"$searchQuery\""
                                } else if (page == 1 && selectedCookbook == null) {
                                    "Select a cookbook to see recipes"
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
                                        onClick = { onRecipeClick(recipe.slug) },
                                        baseUrl = baseUrl
                                    )
                                }

                                if (page == 0 && state.recipes.isNotEmpty()) {
                                    if (state.hasMore) {
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
                                    } else {
                                        item(span = { GridItemSpan(maxLineSpan) }) {
                                            Text(
                                                text = "No more recipes available",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .padding(vertical = 16.dp)
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
