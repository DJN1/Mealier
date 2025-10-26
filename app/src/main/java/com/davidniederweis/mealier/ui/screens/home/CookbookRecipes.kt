package com.davidniederweis.mealier.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.data.model.cookbook.Cookbook
import com.davidniederweis.mealier.ui.components.recipe.RecipeCard
import com.davidniederweis.mealier.ui.components.layout.adaptiveGridCells
import com.davidniederweis.mealier.ui.viewmodel.admin.CookbookManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeListState
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookbookRecipes(
    onRecipeClick: (String) -> Unit,
    selectedCookbook: Cookbook?,
    onCookbookSelected: (Cookbook) -> Unit,
    recipeViewModel: RecipeViewModel = appViewModel(),
    cookbookManagementViewModel: CookbookManagementViewModel = appViewModel()
) {
    val cookbooks by cookbookManagementViewModel.cookbooks.collectAsState()
    val recipeListState by recipeViewModel.recipeListState.collectAsState()
    val baseUrl by recipeViewModel.baseUrl.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(cookbooks) {
        if (cookbooks.isEmpty()) {
            cookbookManagementViewModel.getCookbooks()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.padding(16.dp)
        ) {
            TextField(
                value = selectedCookbook?.name ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select a cookbook") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                cookbooks.forEach { cookbook ->
                    DropdownMenuItem(
                        text = { Text(cookbook.name) },
                        onClick = {
                            onCookbookSelected(cookbook)
                            expanded = false
                        }
                    )
                }
            }
        }

        selectedCookbook?.let { cookbook ->
            LaunchedEffect(cookbook) {
                recipeViewModel.loadRecipesByCookbook(cookbook.slug)
            }

            when (val state = recipeListState) {
                is RecipeListState.Success -> {
                    LazyVerticalGrid(
                        columns = adaptiveGridCells(),
                        contentPadding = PaddingValues(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.recipes) { recipe ->
                            RecipeCard(
                                recipe = recipe,
                                onClick = { onRecipeClick(recipe.slug) },
                                baseUrl = baseUrl
                            )
                        }
                    }
                }
                is RecipeListState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                else -> {
                    // Handle error and empty states
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Please select a cookbook to see the recipes.")
            }
        }
    }
}
