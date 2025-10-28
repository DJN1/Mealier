package com.davidniederweis.mealier.ui.components.layout

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.dp

@Composable
fun adaptiveGridCells(): GridCells {
    val screenWidth = with(LocalDensity.current) {
        LocalWindowInfo.current.containerSize.width.toDp()
    }

    return GridCells.Adaptive(
        minSize = when {
            screenWidth < 600.dp -> 160.dp  // Phone
            screenWidth < 840.dp -> 200.dp  // Small tablet
            else -> 240.dp                   // Large tablet
        }
    )
}
