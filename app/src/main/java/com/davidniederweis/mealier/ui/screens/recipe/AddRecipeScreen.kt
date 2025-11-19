package com.davidniederweis.mealier.ui.screens.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.davidniederweis.mealier.ui.components.addrecipe.ImportUrlForm
import com.davidniederweis.mealier.ui.components.addrecipe.ManualRecipeForm
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.AddRecipeViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeCreationState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddRecipeScreen(
    initialTab: Int = 0,
    onNavigateBack: () -> Unit,
    onRecipeCreated: (String) -> Unit,
    viewModel: AddRecipeViewModel = appViewModel()
) {
    val creationState by viewModel.creationState.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(initialTab) }
    val tabs = listOf("Manual", "Import URL")

    // Handle success state
    LaunchedEffect(creationState) {
        if (creationState is RecipeCreationState.Success) {
            val recipe = (creationState as RecipeCreationState.Success).recipe
            onRecipeCreated(recipe.slug)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Recipe") },
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            // Tabs
            PrimaryTabRow(selectedTabIndex = selectedTabIndex) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(title) }
                    )
                }
            }

            // Tab content
            when (selectedTabIndex) {
                0 -> ManualRecipeForm(
                    viewModel = viewModel,
                    creationState = creationState,
                    submitButtonText = "Create Recipe",
                    onSubmit = { viewModel.createManualRecipe() }
                )
                1 -> ImportUrlForm(
                    viewModel = viewModel,
                    creationState = creationState
                )
            }
        }
    }
}
