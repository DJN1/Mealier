package com.davidniederweis.mealier.ui.components.recipe

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

@Composable
fun InstructionItem(
    stepNumber: Int,
    title: String?,
    text: String?,
    summary: String?,
    isCompleted: Boolean,
    onToggle: () -> Unit
) {
    // Check if we have any content to display
    // Priority: text > summary > title
    val hasTitle = !title.isNullOrBlank()
    val hasText = !text.isNullOrBlank()
    val hasSummary = !summary.isNullOrBlank()

    // If no content at all, don't render
    if (!hasTitle && !hasText && !hasSummary) {
        return
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 6.dp),
        tonalElevation = if (isCompleted) 0.dp else 2.dp,
        color = if (isCompleted) {
            MaterialTheme.colorScheme.surfaceVariant
        } else {
            MaterialTheme.colorScheme.surface
        },
        shape = MaterialTheme.shapes.medium,
        border = if (isCompleted) null else BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val headerText = when {
                    hasTitle -> "$stepNumber. $title"
                    hasSummary -> "$stepNumber. $summary"
                    else -> "Step $stepNumber"
                }

                Text(
                    text = headerText,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.ExtraBold,
                    textDecoration = if (isCompleted) TextDecoration.LineThrough else null,
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            val bodyColor = if (isCompleted) {
                MaterialTheme.colorScheme.onSurfaceVariant
            } else {
                MaterialTheme.colorScheme.onSurface
            }

            val bodyDecoration = if (isCompleted) TextDecoration.LineThrough else null

            if (hasText) {
                Text(
                    text = text!!,
                    style = MaterialTheme.typography.bodyLarge,
                    color = bodyColor,
                    textDecoration = bodyDecoration
                )
            }
        }
    }
}
