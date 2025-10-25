package com.davidniederweis.mealier.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.davidniederweis.mealier.data.model.unit.RecipeUnit
import com.davidniederweis.mealier.ui.components.admin.AddUnitDialog
import com.davidniederweis.mealier.ui.viewmodel.admin.UnitManagementState
import com.davidniederweis.mealier.ui.viewmodel.admin.UnitManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.appViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitManagementScreen(
    navController: NavController,
    viewModel: UnitManagementViewModel = appViewModel()
) {
    val state by viewModel.unitManagementState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedUnit by remember { mutableStateOf<RecipeUnit?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getUnits()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unit Management") },
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
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Unit")
            }
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
                is UnitManagementState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is UnitManagementState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(currentState.units) { unit ->
                            ListItem(
                                headlineContent = { Text(unit.name) },
                                modifier = Modifier.clickable {
                                    selectedUnit = unit
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }
                is UnitManagementState.Error -> {
                    Text(
                        text = currentState.message,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is UnitManagementState.Idle -> {
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
                    showDialog = true
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

    if (showDialog) {
        AddUnitDialog(
            unit = selectedUnit,
            onDismiss = {
                showDialog = false
                selectedUnit = null
            },
            onSave = { name, namePlural, abbreviation, abbreviationPlural ->
                if (selectedUnit == null) {
                    viewModel.createUnit(name, namePlural, abbreviation, abbreviationPlural)
                } else {
                    selectedUnit!!.id?.let { viewModel.updateUnit(it, name, namePlural, abbreviation, abbreviationPlural) }
                }
                showDialog = false
                selectedUnit = null
            }
        )
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Unit") },
            text = { Text("Are you sure you want to delete this unit?") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedUnit?.id?.let { viewModel.deleteUnit(it) }
                        showDeleteDialog = false
                        selectedUnit = null
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
