package com.davidniederweis.mealier.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Webhook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Book
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.davidniederweis.mealier.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdScreen(
    navController: NavController
) {
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
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            item {
                ListItem(
                    headlineContent = { Text("Household Settings") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.HouseholdSettings.route)
                    }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Members") },
                    leadingContent = {
                        Icon(
                            Icons.Default.People,
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Members.route)
                    }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Cookbooks") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Book,
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.CookbookManagement.route)
                    }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Webhooks") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Webhook,
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Webhooks.route)
                    }
                )
            }
            item {
                ListItem(
                    headlineContent = { Text("Notifiers") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Notifications,
                            contentDescription = null
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        navController.navigate(Screen.Notifiers.route)
                    }
                )
            }
        }
    }
}
