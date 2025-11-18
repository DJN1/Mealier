package com.davidniederweis.mealier.ui.components.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.Card
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun ServingsCard(
    baseServings: Double,
    currentServings: Double,
    onServingsChange: (Double) -> Unit,
    canScale: Boolean,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
                .fillMaxHeight(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Restaurant,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Yield",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Format servings display with fraction support
            val servingsText = formatServings(currentServings)
            
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$servingsText Servings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                // Show +/- buttons only if ingredients can be scaled
                if (canScale) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilledTonalIconButton(
                            onClick = { 
                                if (currentServings > 1.0) {
                                    onServingsChange(currentServings - 1.0)
                                }
                            },
                            modifier = Modifier.size(32.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Remove,
                                contentDescription = "Decrease servings",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        
                        Text(
                            text = if (baseServings > 0) "${formatMultiplier(currentServings / baseServings)}x" else "1x",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        FilledTonalIconButton(
                            onClick = { 
                                onServingsChange(currentServings + 1.0)
                            },
                            modifier = Modifier.size(32.dp),
                            colors = IconButtonDefaults.filledTonalIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Increase servings",
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // Spacer with same height as the top row to balance the spacing
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

/**
 * Format servings count with fraction support (up to 3 decimals)
 */
private fun formatServings(servings: Double): String {
    val intPart = servings.toInt()
    val fracPart = servings - intPart
    
    // Common fractions with their decimal values
    val fractions = listOf(
        0.125 to "⅛",
        0.25 to "¼",
        0.333 to "⅓",
        0.375 to "⅜",
        0.5 to "½",
        0.625 to "⅝",
        0.667 to "⅔",
        0.75 to "¾",
        0.875 to "⅞"
    )
    
    // Tolerance for fraction matching (about 1/32)
    val tolerance = 0.04
    
    // Find the closest fraction
    val closestFraction = fractions.minByOrNull { (value, _) ->
        kotlin.math.abs(fracPart - value)
    }
    
    val fractionStr = if (closestFraction != null && kotlin.math.abs(fracPart - closestFraction.first) <= tolerance) {
        closestFraction.second
    } else if (fracPart < 0.001) {
        ""
    } else {
        // Format to 3 decimal places and remove trailing zeros
        val formatted = String.format(Locale.ROOT, "%.3f", fracPart)
        formatted.trimEnd('0').trimEnd('.')
    }
    
    return when {
        intPart > 0 && fractionStr.isNotEmpty() -> "$intPart $fractionStr".trim()
        intPart > 0 -> intPart.toString()
        fractionStr.isNotEmpty() -> fractionStr
        else -> servings.toString()
    }
}

/**
 * Format multiplier with fraction support (up to 3 decimals)
 */
private fun formatMultiplier(multiplier: Double): String {
    val intPart = multiplier.toInt()
    val fracPart = multiplier - intPart
    
    // Common fractions with their decimal values
    val fractions = listOf(
        0.125 to "⅛",
        0.25 to "¼",
        0.333 to "⅓",
        0.375 to "⅜",
        0.5 to "½",
        0.625 to "⅝",
        0.667 to "⅔",
        0.75 to "¾",
        0.875 to "⅞"
    )
    
    // Tolerance for fraction matching (about 1/32)
    val tolerance = 0.04
    
    // Find the closest fraction
    val closestFraction = fractions.minByOrNull { (value, _) ->
        kotlin.math.abs(fracPart - value)
    }
    
    val fracStr = if (closestFraction != null && kotlin.math.abs(fracPart - closestFraction.first) <= tolerance) {
        closestFraction.second
    } else if (fracPart < 0.001) {
        ""
    } else {
        // Format to 3 decimal places and remove trailing zeros
        val formatted = "%.3f".format(fracPart)
        formatted.trimEnd('0').trimEnd('.')
    }
    
    return when {
        intPart > 0 && fracStr.isNotEmpty() -> "$intPart$fracStr"
        intPart > 0 -> intPart.toString()
        fracStr.isNotEmpty() -> fracStr
        else -> {
            val formatted = String.format(Locale.ROOT, "%.3f", multiplier)
            formatted.trimEnd('0').trimEnd('.')
        }
    }
}
