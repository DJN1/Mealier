
package com.davidniederweis.mealier.ui.screens.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.SortByAlpha
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.davidniederweis.mealier.data.model.user.User
import com.davidniederweis.mealier.ui.viewmodel.admin.UserManagementState
import com.davidniederweis.mealier.ui.viewmodel.admin.UserManagementViewModel
import com.davidniederweis.mealier.ui.viewmodel.appViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersScreen(
    navController: NavController,
    viewModel: UserManagementViewModel = appViewModel()
) {
    val state by viewModel.userManagementState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showInviteDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var inviteEmail by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.getUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Members") },
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
                onClick = { showInviteDialog = true },
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Invite User")
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
                is UserManagementState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is UserManagementState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(currentState.users) { user ->
                            ListItem(
                                headlineContent = { Text(user.username) },
                                supportingContent = { Text(user.email) },
                                modifier = Modifier.clickable {
                                    selectedUser = user
                                    showBottomSheet = true
                                }
                            )
                        }
                    }
                }
                is UserManagementState.Error -> {
                    Text(
                        text = currentState.message,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                is UserManagementState.Idle -> {
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
                    showEditDialog = true
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
            title = { Text("Delete User") },
            text = { Text("Are you sure you want to delete this user?") },
            confirmButton = {
                Button(
                    onClick = {
                        selectedUser?.id?.let { viewModel.deleteUser(it) }
                        showDeleteDialog = false
                        selectedUser = null
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

    if (showInviteDialog) {
        AlertDialog(
            onDismissRequest = { showInviteDialog = false },
            title = { Text("Invite User") },
            text = {
                Column {
                    Text("Enter the email address of the user you want to invite.")
                    Spacer(modifier = Modifier.height(16.dp))
                    TextField(
                        value = inviteEmail,
                        onValueChange = { inviteEmail = it },
                        label = { Text("Email") }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.inviteUser(inviteEmail)
                        showInviteDialog = false
                        inviteEmail = ""
                    }
                ) {
                    Text("Invite")
                }
            },
            dismissButton = {
                Button(onClick = { showInviteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditDialog) {
        selectedUser?.let {
            EditUserDialog(
                user = it,
                onDismiss = { showEditDialog = false },
                onSave = { user ->
                    viewModel.updateUser(user)
                    showEditDialog = false
                }
            )
        }
    }
}
