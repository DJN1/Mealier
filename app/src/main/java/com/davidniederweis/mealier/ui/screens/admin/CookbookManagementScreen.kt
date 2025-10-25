package com.davidniederweis.mealier.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.davidniederweis.mealier.data.model.recipe.RecipeSummary
import com.davidniederweis.mealier.ui.navigation.Screen
import com.davidniederweis.mealier.ui.viewmodel.admin.CookbookManagementState
import com.davidniederweis.mealier.ui.viewmodel.admin.CookbookManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.appViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookbookManagementScreen(
    navController: NavController,
    viewModel: CookbookManagementViewModel = appViewModel()
) {
    val state by viewModel.cookbookManagementState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedRecipe by remember { mutableStateOf<RecipeSummary?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getRecipes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cookbook Management") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSort() }) {
                        Icon(
                            imageVector = Icons.Default.SortByAlpha,
                            contentDescription = "Sort"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.search(it) },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.search("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Search"
                            )
                        }
                    }
                }
            )

            when (val currentState = state) {
                is CookbookManagementState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is CookbookManagementState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(currentState.recipes) { recipe ->
                            ListItem(
                                headlineContent = { Text(recipe.name ?: "") },
                                modifier = Modifier.clickable {
                                    selectedRecipe = recipe
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }
                is CookbookManagementState.Error -> {
                    Text(
                        text = currentState.message,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is CookbookManagementState.Idle -> {
                    // Do nothing
                }
            }
        }
    }

    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            ListItem(
                headlineContent = { Text("Edit") },
                leadingContent = {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                },
                modifier = Modifier.clickable {
                    showBottomSheet = false
                    selectedRecipe?.slug?.let { navController.navigate(Screen.EditRecipe.createRoute(it)) }
                }
            )
            ListItem(
                headlineContent = { Text("Delete") },
                leadingContent = {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                },
                modifier = Modifier.clickable {
                    showBottomSheet = false
                    showDeleteDialog = true
                }
            )
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Recipe") },
            text = { Text("Are you sure you want to delete this recipe?") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedRecipe?.slug?.let { viewModel.deleteRecipe(it) }
                        showDeleteDialog = false
                        selectedRecipe = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
