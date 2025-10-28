package com.davidniederweis.mealier.ui.components.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.data.model.ingredient.RecipeIngredient


@Composable
fun IngredientItem(
    modifier: Modifier = Modifier,
    ingredient: RecipeIngredient,
    servingsMultiplier: Double = 1.0,
    isGathered: Boolean,
    onToggle: () -> Unit,
) {
    val annotatedText = formatIngredientAnnotated(ingredient, servingsMultiplier, isGathered)

    if (annotatedText.text.isNotBlank()) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(20.dp) // Constrain height to be tight with text
                .clickable(onClick = onToggle),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = isGathered,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    checkmarkColor = MaterialTheme.colorScheme.onPrimary
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = annotatedText,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
        }
    }
}


/**
 * Formats an ingredient as AnnotatedString with italic notes and optional strikethrough.
 * Scales the quantity by the servings multiplier if the ingredient is parsed.
 */
private fun formatIngredientAnnotated(
    ingredient: RecipeIngredient,
    servingsMultiplier: Double,
    isGathered: Boolean
): AnnotatedString {
    return buildAnnotatedString {
        // Apply strikethrough if gathered
        val baseStyle = if (isGathered) {
            SpanStyle(textDecoration = TextDecoration.LineThrough)
        } else {
            SpanStyle()
        }

        // Use original text if available (unparsed ingredient)
        ingredient.originalText?.let { original ->
            if (original.isNotBlank()) {
                withStyle(baseStyle) {
                    append(original)
                }
                return@buildAnnotatedString
            }
        }

        // Build from parsed components
        val parts = mutableListOf<String>()

        // Add quantity if not disabled and > 0
        if (!ingredient.disableAmount && ingredient.quantity > 0.0) {
            // Scale quantity if ingredient has unit and food (parsed ingredient)
            val scaledQuantity = if (ingredient.unit != null && ingredient.food != null) {
                ingredient.quantity * servingsMultiplier
            } else {
                ingredient.quantity
            }
            
            val quantityInt = scaledQuantity.toInt()

            // Format as integer if no decimal part
            val quantityStr = if (scaledQuantity == quantityInt.toDouble()) {
                quantityInt.toString()
            } else {
                // Use helper function to format with fractions
                formatQuantityWithFraction(scaledQuantity)
            }
            parts.add(quantityStr)
        }

        // Add unit if present
        ingredient.unit?.let { unit ->
            val unitText = if (unit.useAbbreviation && !unit.abbreviation.isNullOrBlank()) {
                unit.abbreviation
            } else {
                unit.name
            }

            if (unitText.isNotBlank()) {
                parts.add(unitText)
            }
        }

        // Add food name if present
        ingredient.food?.let { food ->
            if (food.name.isNotBlank()) {
                parts.add(food.name)
            }
        }

        // Build and append the main text
        val mainText = parts.joinToString(" ")
        if (mainText.isNotBlank()) {
            withStyle(baseStyle) {
                append(mainText)
            }
        }

        // Add note if present (italicized)
        ingredient.note?.let { note ->
            if (note.isNotBlank()) {
                if (mainText.isNotBlank()) {
                    withStyle(baseStyle) {
                        append(", ")
                    }
                }
                withStyle(baseStyle.copy(fontStyle = FontStyle.Italic)) {
                    append(note)
                }
            }
        }

        // Fallback if nothing was added
        if (this.length == 0) {
            withStyle(baseStyle) {
                append("Unknown ingredient")
            }
        }
    }
}

/**
 * Format a quantity with proper fraction support.
 * Finds the closest common fraction within tolerance.
 */
private fun formatQuantityWithFraction(quantity: Double): String {
    val intPart = quantity.toInt()
    val fracPart = quantity - intPart
    
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
        intPart > 0 && fracStr.isNotEmpty() -> "$intPart $fracStr".trim()
        intPart > 0 -> intPart.toString()
        fracStr.isNotEmpty() -> fracStr
        else -> quantity.toString()
    }
}