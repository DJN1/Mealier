package com.davidniederweis.mealier.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
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
import com.davidniederweis.mealier.ui.navigation.Screen
import com.davidniederweis.mealier.ui.viewmodel.admin.CookbookManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.appViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookbookManagementScreen(
    navController: NavController,
    viewModel: CookbookManagementViewModel = appViewModel()
) {
    val cookbooks by viewModel.cookbooks.collectAsState()
    var hideCookbooks by remember { mutableStateOf(false) }

    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.getCookbooks()
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
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(onClick = { navController.navigate(Screen.CreateCookbook.route) }) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Create")
                    Text("Create")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hideCookbooks, onCheckedChange = { hideCookbooks = it })
                    Text("Hide Cookbooks from Other Households")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(cookbooks) { cookbook ->
                    ListItem(
                        headlineContent = { Text(cookbook.name) },
                        supportingContent = { Text(cookbook.description) },
                        modifier = Modifier.clickable { navController.navigate(Screen.EditCookbook.createRoute(cookbook.id)) }
                    )
                }
            }
        }
    }
}
