package com.davidniederweis.mealier.ui.components.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
    ingredient: RecipeIngredient,
    isGathered: Boolean,
    onToggle: () -> Unit
) {
    val annotatedText = formatIngredientAnnotated(ingredient, isGathered)

    if (annotatedText.text.isNotBlank()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "•",
                style = MaterialTheme.typography.bodyLarge,
                color = if (isGathered) {
                    MaterialTheme.colorScheme.onSurfaceVariant
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
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
 */
private fun formatIngredientAnnotated(
    ingredient: RecipeIngredient,
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
            val quantity = ingredient.quantity
            val quantityInt = quantity.toInt()

            // Format as integer if no decimal part
            val quantityStr = if (quantity == quantityInt.toDouble()) {
                quantityInt.toString()
            } else {
                // Format fractions nicely
                when (quantity) {
                    0.25 -> "¼"
                    0.33, 0.333 -> "⅓"
                    0.5 -> "½"
                    0.66, 0.666, 0.67 -> "⅔"
                    0.75 -> "¾"
                    else -> quantity.toString()
                }
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