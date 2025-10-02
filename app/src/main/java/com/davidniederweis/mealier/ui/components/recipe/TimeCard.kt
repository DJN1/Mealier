package com.davidniederweis.mealier.ui.components.recipe

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.data.model.recipe.RecipeDetail

@Composable
fun TimeCard(
    recipe: RecipeDetail,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxHeight()) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Time",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                recipe.prepTime?.let { prepTime ->
                    if (prepTime.isNotBlank()) {
                        TimeRow(label = "Prep", time = prepTime)
                    }
                }

                recipe.performTime?.let { cookTime ->
                    if (cookTime.isNotBlank() && recipe.cookTime.isNullOrBlank()) {
                        TimeRow(label = "Cook", time = cookTime)
                    }
                }

                recipe.cookTime?.let { cookTime ->
                    if (cookTime.isNotBlank() && recipe.performTime.isNullOrBlank()) {
                        TimeRow(label = "Cook", time = cookTime)
                    }
                }

                recipe.totalTime?.let { totalTime ->
                    if (totalTime.isNotBlank()) {
                        TimeRow(label = "Total", time = totalTime)
                    }
                }
            }
        }
    }
}
