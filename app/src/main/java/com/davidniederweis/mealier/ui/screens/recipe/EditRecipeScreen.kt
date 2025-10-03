package com.davidniederweis.mealier.ui.screens.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.davidniederweis.mealier.ui.components.addrecipe.ManualRecipeForm
import com.davidniederweis.mealier.ui.components.general.ErrorMessage
import com.davidniederweis.mealier.ui.components.general.LoadingBox
import com.davidniederweis.mealier.ui.components.layout.BottomNavBar
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.EditRecipeViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.LoadingState
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeCreationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeScreen(
    slug: String,
    onNavigateBack: () -> Unit,
    onRecipeUpdated: (String) -> Unit,
    navController: NavController,
    isAdmin: Boolean,
    viewModel: EditRecipeViewModel = appViewModel()
) {
    val updateState by viewModel.updateState.collectAsState()
    val loadingState by viewModel.loadingState.collectAsState()

    // Load recipe on first composition
    LaunchedEffect(slug) {
        viewModel.loadRecipe(slug)
    }

    // Handle success state
    LaunchedEffect(updateState) {
        if (updateState is RecipeCreationState.Success) {
            val recipe = (updateState as RecipeCreationState.Success).recipe
            onRecipeUpdated(recipe.slug)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Recipe") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
        }
    ) { paddingValues ->
        when (loadingState) {
            is LoadingState.Loading -> {
                LoadingBox(modifier = Modifier.fillMaxSize().padding(paddingValues))
            }
            is LoadingState.Error -> {
                ErrorMessage(
                    message = (loadingState as LoadingState.Error).message,
                    onRetry = { viewModel.loadRecipe(slug) },
                    modifier = Modifier.fillMaxSize().padding(paddingValues)
                )
            }
            is LoadingState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    ManualRecipeForm(
                        viewModel = viewModel,
                        creationState = updateState,
                        submitButtonText = "Update Recipe",
                        onSubmit = { viewModel.updateRecipe() }
                    )
                }
            }
            is LoadingState.Idle -> {
                LoadingBox(modifier = Modifier.fillMaxSize().padding(paddingValues))
            }
        }
    }
}
