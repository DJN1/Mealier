package com.davidniederweis.mealier.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.davidniederweis.mealier.ui.components.layout.BottomNavBar
import com.davidniederweis.mealier.ui.components.general.ErrorMessage
import com.davidniederweis.mealier.ui.components.general.LoadingBox
import com.davidniederweis.mealier.ui.components.profile.ProfileInfoRow
import com.davidniederweis.mealier.ui.components.profile.SettingRow
import com.davidniederweis.mealier.ui.navigation.Screen
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.BiometricsViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.SettingsViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.ThemeViewModel
import com.davidniederweis.mealier.ui.viewmodel.profile.ProfileState
import com.davidniederweis.mealier.ui.viewmodel.profile.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    navController: NavController,
    isAdmin: Boolean,
    profileViewModel: ProfileViewModel = appViewModel(),
    themeViewModel: ThemeViewModel = appViewModel(),
    biometricsViewModel: BiometricsViewModel = appViewModel(),
    settingsViewModel: SettingsViewModel = appViewModel()
) {
    val profileState by profileViewModel.profileState.collectAsState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val biometricEnabled by biometricsViewModel.biometricEnabled.collectAsState()
    val keepScreenOn by settingsViewModel.keepScreenOn.collectAsState()

    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") }
            )
        },
        bottomBar = {
            BottomNavBar(
                navController = navController,
                isAdmin = isAdmin
            )
        }
    ) { paddingValues ->
        when (val state = profileState) {
            is ProfileState.Loading -> {
                LoadingBox(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is ProfileState.Success -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Card
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Column {
                                    Text(
                                        text = state.user.fullName ?: state.user.username ?: "User",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                                    )
                                    state.user.email?.let { email ->
                                        Text(
                                            text = email,
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }

                            HorizontalDivider()

                            // User Info
                            ProfileInfoRow(
                                label = "Username",
                                value = state.user.username ?: "N/A"
                            )

                            if (state.user.admin) {
                                ProfileInfoRow(
                                    label = "Role",
                                    value = "Administrator"
                                )
                            }

                            state.user.group?.let { group ->
                                ProfileInfoRow(
                                    label = "Group",
                                    value = group
                                )
                            }
                        }
                    }

                    // Settings Card
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Settings",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                            )

                            HorizontalDivider()

                            // Dark Mode Toggle
                            SettingRow(
                                icon = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                                title = "Dark Mode",
                                description = "Switch between light and dark theme",
                                checked = isDarkMode,
                                onCheckedChange = { enabled ->
                                    themeViewModel.setDarkMode(enabled)
                                }
                            )

                            HorizontalDivider()

                            // Biometric Toggle
                            SettingRow(
                                icon = Icons.Default.Fingerprint,
                                title = "Biometric Authentication",
                                description = "Use fingerprint or face unlock",
                                checked = biometricEnabled,
                                onCheckedChange = { enabled ->
                                    biometricsViewModel.setBiometricEnabled(enabled)
                                }
                            )

                            HorizontalDivider()

                            // Keep Screen On Toggle
                            SettingRow(
                                icon = Icons.Default.Visibility,
                                title = "Keep Screen On",
                                description = "Keep screen awake on recipe detail",
                                checked = keepScreenOn,
                                onCheckedChange = { enabled ->
                                    settingsViewModel.setKeepScreenOn(enabled)
                                }
                            )
                        }
                    }

                    Button(
                        onClick = { navController.navigate(Screen.Favorites.route) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Favorite Recipes")
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Logout Button
                    Button(
                        onClick = onLogout,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = null
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Logout")
                    }
                }
            }

            is ProfileState.Error -> {
                ErrorMessage(
                    message = state.message,
                    onRetry = { profileViewModel.loadProfile() },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }

            is ProfileState.Idle -> {
                LoadingBox(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                )
            }
        }
    }
}

