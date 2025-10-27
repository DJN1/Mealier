package com.davidniederweis.mealier.ui.screens.recipe

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.davidniederweis.mealier.BuildConfig
<<<<<<< Updated upstream
import com.davidniederweis.mealier.data.model.category.Category
import com.davidniederweis.mealier.data.model.recipe.RecipeCategory
import com.davidniederweis.mealier.data.model.recipe.RecipeTag
import com.davidniederweis.mealier.data.model.tag.Tag
||||||| Stash base
import com.davidniederweis.mealier.ui.components.layout.BottomNavBar
=======
import com.davidniederweis.mealier.data.model.recipe.RecipeCategory
import com.davidniederweis.mealier.data.model.recipe.RecipeTag
>>>>>>> Stashed changes
import com.davidniederweis.mealier.ui.components.general.ErrorMessage
import com.davidniederweis.mealier.ui.components.general.LoadingBox
import com.davidniederweis.mealier.ui.components.layout.BottomNavBar
import com.davidniederweis.mealier.ui.components.recipe.RecipeDetailContent
import com.davidniederweis.mealier.ui.navigation.Screen
import com.davidniederweis.mealier.ui.screens.recipe.EditRecipeTagsAndCategoriesSheet
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
    val baseUrl by viewModel.baseUrl.collectAsState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteError by remember { mutableStateOf<String?>(null) }
<<<<<<< Updated upstream
    var showEditTagsAndCategoriesSheet by remember { mutableStateOf(false) }
||||||| Stash base
=======
    var showEditTagsSheet by remember { mutableStateOf(false) }
    var showEditCategoriesSheet by remember { mutableStateOf(false) }
>>>>>>> Stashed changes

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
                        // Edit tags and categories button (admin only)
                        if (isAdmin) {
                            IconButton(onClick = { showEditTagsAndCategoriesSheet = true }) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Tags and Categories"
                                )
                            }
                        }
                        // Delete button (admin only)
                        if (isAdmin) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Recipe"
                                )
                            }
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
                    recipe = state.recipe,
                    baseUrl = baseUrl,
                    isAdmin = isAdmin,
                    onEditCategoriesClick = { showEditCategoriesSheet = true },
                    onEditTagsClick = { showEditTagsSheet = true }
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

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Recipe?") },
            text = { 
                Column {
                    Text("Are you sure you want to delete this recipe? This action cannot be undone.")
                    deleteError?.let { error ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteRecipe(
                            slug = slug,
                            onSuccess = {
                                showDeleteDialog = false
                                // Refresh the recipes list before navigating back
                                viewModel.refresh()
                                onNavigateBack()
                            },
                            onError = { error ->
                                deleteError = error
                            }
                        )
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { 
                    showDeleteDialog = false
                    deleteError = null
                }) {
                    Text("Cancel")
                }
            }
        )
    }
<<<<<<< Updated upstream

    if (showEditTagsAndCategoriesSheet) {
        val state = recipeDetailState as? RecipeDetailState.Success
        if (state != null) {
            val allTags by viewModel.allTags.collectAsState()
            val allCategories by viewModel.allCategories.collectAsState()

            EditRecipeTagsAndCategoriesSheet(
                recipeTags = state.recipe.tags ?: emptyList(),
                recipeCategories = state.recipe.recipeCategory ?: emptyList(),
                allTags = allTags.map { RecipeTag(it.id, it.name, it.name.lowercase().replace(" ", "-")) },
                allCategories = allCategories.map { RecipeCategory(it.id, it.name, it.slug) },
                onTagsChange = { recipeTags ->
                    viewModel.updateRecipeTags(recipeTags.map { Tag(it.id, it.name) })
                },
                onCategoriesChange = { recipeCategories ->
                    viewModel.updateRecipeCategories(recipeCategories.mapNotNull { recipeCategory ->
                        recipeCategory.id?.let { Category(it, recipeCategory.name, recipeCategory.slug) }
                    })
                },
                onDismiss = { showEditTagsAndCategoriesSheet = false }
            )
        }
    }
}
||||||| Stash base
}
=======

    if (showEditCategoriesSheet) {
        val state = recipeDetailState as? RecipeDetailState.Success
        if (state != null) {
            val allCategories by viewModel.allCategories.collectAsState()

            EditRecipeCategoriesSheet(
                recipeCategories = state.recipe.recipeCategory ?: emptyList(),
                allCategories = allCategories.map { RecipeCategory(it.id, it.name, it.slug) },
                onCategoriesChange = { recipeCategories ->
                    viewModel.updateRecipeCategories(recipeCategories)
                },
                onDismiss = { showEditCategoriesSheet = false }
            )
        }
    }

    if (showEditTagsSheet) {
        val state = recipeDetailState as? RecipeDetailState.Success
        if (state != null) {
            val allTags by viewModel.allTags.collectAsState()

            EditRecipeTagsSheet(
                recipeTags = state.recipe.tags ?: emptyList(),
                allTags = allTags.map { RecipeTag(it.id, it.name, it.slug) },
                onTagsChange = { recipeTags ->
                    viewModel.updateRecipeTags(recipeTags)
                },
                onDismiss = { showEditTagsSheet = false }
            )
        }
    }
}
>>>>>>> Stashed changes
