package com.davidniederweis.mealier.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.Flow

/**
 * Observe events from a Flow and handle navigation
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(flow, lifecycleOwner.lifecycle) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect(onEvent)
        }
    }
}

/**
 * Navigate and clear back stack
 */
fun NavController.navigateAndClearBackStack(route: String) {
    navigate(route) {
        popUpTo(0) { inclusive = true }
    }
}

/**
 * Navigate with single top
 */
fun NavController.navigateSingleTop(route: String) {
    navigate(route) {
        launchSingleTop = true
    }
}

/**
 * Check if current destination is the given route
 */
fun NavController.isCurrentRoute(route: String): Boolean {
    return currentDestination?.route == route
}