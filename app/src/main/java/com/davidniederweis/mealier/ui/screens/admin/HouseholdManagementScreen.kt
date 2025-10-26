package com.davidniederweis.mealier.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.davidniederweis.mealier.data.model.household.Household
import com.davidniederweis.mealier.ui.viewmodel.admin.HouseholdManagementState
import com.davidniederweis.mealier.ui.viewmodel.admin.HouseholdManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.appViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdManagementScreen(
    navController: NavController,
    viewModel: HouseholdManagementViewModel = appViewModel()
) {
    val state by viewModel.householdManagementState.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedHousehold by remember { mutableStateOf<Household?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getHouseholds()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Household Management") },
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* TODO: Add household */ },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Household")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is HouseholdManagementState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is HouseholdManagementState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(currentState.households) { household ->
                            ListItem(
                                headlineContent = { Text(household.name) },
                                modifier = Modifier.clickable {
                                    selectedHousehold = household
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }
                is HouseholdManagementState.Error -> {
                    Text(
                        text = currentState.message,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is HouseholdManagementState.Idle -> {
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
                    // TODO: Edit household
                }
            )
            ListItem(
                headlineContent = { Text("Delete") },
                leadingContent = {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                },
                modifier = Modifier.clickable {
                    showBottomSheet = false
                    // TODO: Delete household
                }
            )
        }
    }
}
