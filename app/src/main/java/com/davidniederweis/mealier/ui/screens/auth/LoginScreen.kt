package com.davidniederweis.mealier.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.davidniederweis.mealier.data.security.BiometricAuthHelper
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.auth.AuthState
import com.davidniederweis.mealier.ui.viewmodel.auth.AuthViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.BiometricsViewModel
import com.davidniederweis.mealier.ui.viewmodel.preferences.ServerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    authViewModel: AuthViewModel = appViewModel(),
    biometricsViewModel: BiometricsViewModel = appViewModel(),
    serverViewModel: ServerViewModel = appViewModel()
) {
    val authState by authViewModel.authState.collectAsState()
    val showBiometricPrompt by authViewModel.showBiometricPrompt.collectAsState()
    val biometricEnabled by biometricsViewModel.biometricEnabled.collectAsState()
    val serverUrl by serverViewModel.serverUrl.collectAsState()

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    var serverUrlInput by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var enableBiometric by remember { mutableStateOf(false) }
    var biometricTriggered by remember { mutableStateOf(false) }
    
    // Initialize server URL input from saved value
    LaunchedEffect(serverUrl) {
        if (serverUrlInput.isEmpty() && serverUrl.isNotBlank()) {
            serverUrlInput = serverUrl
        }
    }

    // Handle biometric prompt when app starts
    // Only show if biometric is enabled in settings AND showBiometricPrompt is true
    LaunchedEffect(showBiometricPrompt, biometricEnabled) {
        if (showBiometricPrompt && biometricEnabled && !biometricTriggered) {
            biometricTriggered = true
            val activity = context as? FragmentActivity
            if (activity != null) {
                val biometricHelper = BiometricAuthHelper(context)
                if (biometricHelper.canAuthenticateWithBiometrics()) {
                    biometricHelper.authenticate(
                        activity = activity,
                        onSuccess = {
                            authViewModel.onBiometricSuccess()
                        },
                        onError = { errorMessage ->
                            // User can choose to skip or retry
                            authViewModel.skipBiometric()
                        }
                    )
                } else {
                    authViewModel.skipBiometric()
                }
            } else {
                authViewModel.skipBiometric()
            }
        } else if (showBiometricPrompt && !biometricEnabled) {
            // If biometric is disabled in settings, skip the prompt
            authViewModel.skipBiometric()
        }
    }

    // Handle login success
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onLoginSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sign In") }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo or Title
            Text(
                text = "Mealier",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(48.dp))
            
            // Server URL Field
            OutlinedTextField(
                value = serverUrlInput,
                onValueChange = { serverUrlInput = it },
                label = { Text("Server URL") },
                placeholder = { Text("https://your-mealie-instance.com") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = authState !is AuthState.Loading,
                supportingText = {
                    Text("Enter the URL of your Mealie instance")
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // Username Field
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                singleLine = true,
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = if (passwordVisible) {
                                "Hide password"
                            } else {
                                "Show password"
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        if (serverUrlInput.isNotBlank() && username.isNotBlank() && password.isNotBlank()) {
                            scope.launch {
                                serverViewModel.setServerUrl(serverUrlInput)
                                authViewModel.login(username, password, enableBiometric)
                            }
                        }
                    }
                ),
                enabled = authState !is AuthState.Loading
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Biometric checkbox (only show if device supports it)
            val biometricHelper = remember { BiometricAuthHelper(context) }
            if (biometricHelper.canAuthenticateWithBiometrics()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Checkbox(
                        checked = enableBiometric,
                        onCheckedChange = { enableBiometric = it },
                        enabled = authState !is AuthState.Loading
                    )
                    Text(
                        text = "Enable biometric authentication",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button
            Button(
                onClick = {
                    scope.launch {
                        serverViewModel.setServerUrl(serverUrlInput)
                        authViewModel.login(username, password, enableBiometric)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = serverUrlInput.isNotBlank() &&
                        username.isNotBlank() &&
                        password.isNotBlank() &&
                        authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Text("Sign In")
                }
            }

            // Error Message
            if (authState is AuthState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
