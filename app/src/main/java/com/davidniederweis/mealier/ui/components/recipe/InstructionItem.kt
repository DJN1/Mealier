package com.davidniederweis.mealier.ui.components.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = if (isCompleted) {
                MaterialTheme.colorScheme.surfaceVariant
            } else {
                MaterialTheme.colorScheme.primaryContainer
            },
            modifier = Modifier.size(28.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = stepNumber.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    },
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    }
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Show title if exists
            if (hasTitle) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    },
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }

            // Show text if exists, otherwise show summary
            if (hasText) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    },
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            } else if (hasSummary) {
                Text(
                    text = summary,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = if (isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    },
                    color = if (isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
            }
        }
    }
}
