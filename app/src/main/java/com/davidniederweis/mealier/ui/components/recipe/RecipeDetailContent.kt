package com.davidniederweis.mealier.ui.components.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.davidniederweis.mealier.BuildConfig
import com.davidniederweis.mealier.data.model.recipe.RecipeDetail

@Composable
fun RecipeDetailContent(
    recipe: RecipeDetail,
    modifier: Modifier = Modifier
) {
    // Track gathered ingredients by their index
    val gatheredIngredients = remember { mutableStateSetOf<Int>() }
    // Track completed instructions by their index
    val completedInstructions = remember { mutableStateSetOf<Int>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Recipe Image
        AsyncImage(
            model = "${BuildConfig.BASE_URL}/api/media/recipes/${recipe.id}/images/original.webp",
            contentDescription = recipe.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(horizontal = 16.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Recipe Title and Description
            Text(
                text = recipe.name ?: "Untitled Recipe",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            recipe.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Recipe Info Cards
            val hasTimeData = hasTimeData(recipe)
            val servingsDisplay = getServingsDisplay(recipe)
            val hasServingsData = servingsDisplay != null

            when {
                // Both cards have data - match heights
                hasTimeData && hasServingsData -> {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        TimeCard(
                            recipe = recipe,
                            modifier = Modifier.weight(1f)
                        )
                        ServingsCard(
                            servingsDisplay = servingsDisplay,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                // Only time data
                hasTimeData -> {
                    TimeCard(
                        recipe = recipe,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // Only servings data
                hasServingsData -> {
                    ServingsCard(
                        servingsDisplay = servingsDisplay,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                // No data - show nothing
            }

            // Ingredients Section
            recipe.recipeIngredient.let { ingredients ->
                if (ingredients.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Ingredients",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            HorizontalDivider()

                            ingredients.forEachIndexed { index, ingredient ->
                                // Check if this is a section title
                                if (!ingredient.title.isNullOrBlank()) {
                                    // Section header (not tappable)
                                    Text(
                                        text = ingredient.title,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                    )
                                } else {
                                    // Regular ingredient (tappable)
                                    val isGathered = gatheredIngredients.contains(index)
                                    IngredientItem(
                                        ingredient = ingredient,
                                        isGathered = isGathered,
                                        onToggle = {
                                            if (isGathered) {
                                                gatheredIngredients.remove(index)
                                            } else {
                                                gatheredIngredients.add(index)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Instructions Section
            if (!recipe.recipeInstructions.isNullOrEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Instructions",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    recipe.recipeInstructions.forEachIndexed { index, recipeStep ->
                        InstructionItem(
                            stepNumber = index + 1,
                            title = recipeStep.title,
                            text = recipeStep.text,
                            summary = recipeStep.summary,
                            isCompleted = completedInstructions.contains(index),
                            onToggle = {
                                if (completedInstructions.contains(index)) {
                                    completedInstructions.remove(index)
                                } else {
                                    completedInstructions.add(index)
                                }
                            }
                        )
                    }
                }
            }


            // Notes Section
            recipe.notes?.let { notes ->
                if (notes.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                                Text(
                                    text = "Notes",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                            notes.forEach { note ->
                                Text(
                                    text = note.text,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Bottom spacing for nav bar
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}


/**
 * Determines the servings/yield display text based on available data.
 * Returns null if no data is available.
 */
private fun getServingsDisplay(recipe: RecipeDetail): String? {
    // Rule 1: If servings is not null and greater than 0
    val recipeYield = recipe.recipeYield
    if (recipeYield != null) {
        val servings = recipeYield.toDoubleOrNull()
        if (servings != null && servings > 0.0) {
            val servingsInt = servings.toInt()
            return if (servings == servingsInt.toDouble()) {
                "$servingsInt Servings"
            } else {
                "$servings Servings"
            }
        }
    }

    // Rule 2: If servings is null or zero, check yield and yieldQuantity
    val yieldAmount = recipe.recipeYieldQuantity
    val yieldUnit = recipe.recipeYield

    if (yieldAmount != null && yieldAmount > 0.0 && !yieldUnit.isNullOrBlank()) {
        val yieldInt = yieldAmount.toInt()
        return if (yieldAmount == yieldInt.toDouble()) {
            "$yieldInt $yieldUnit"
        } else {
            "$yieldAmount $yieldUnit"
        }
    }

    // Rule 3: No data available
    return null
}

/**
 * Checks if the recipe has any time data to display.
 */
private fun hasTimeData(recipe: RecipeDetail): Boolean {
    return (!recipe.prepTime.isNullOrBlank()) ||
            (!recipe.cookTime.isNullOrBlank()) ||
            (!recipe.performTime.isNullOrBlank()) ||
            (!recipe.totalTime.isNullOrBlank())
}
