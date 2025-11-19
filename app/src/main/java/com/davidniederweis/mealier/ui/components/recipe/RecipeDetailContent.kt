package com.davidniederweis.mealier.ui.components.recipe

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil.compose.AsyncImage
import com.davidniederweis.mealier.data.model.nutrition.Nutrition
import com.davidniederweis.mealier.data.model.recipe.RecipeDetail
import com.davidniederweis.mealier.data.model.recipe.RecipeCategory
import com.davidniederweis.mealier.data.model.recipe.RecipeTag

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecipeDetailContent(
    recipe: RecipeDetail,
    baseUrl: String,
    modifier: Modifier = Modifier,
    isAdmin: Boolean,
    onEditCategoriesClick: () -> Unit,
    onEditTagsClick: () -> Unit,
) {
    // Track gathered ingredients by their index
    val gatheredIngredients = remember { mutableStateSetOf<Int>() }
    // Track completed instructions by their index
    val completedInstructions = remember { mutableStateSetOf<Int>() }

    // Track current servings count for scaling ingredients
    var currentServings by remember { mutableDoubleStateOf(recipe.recipeServings) }

    // Calculate servings multiplier from current servings
    val servingsMultiplier = if (recipe.recipeServings > 0) {
        currentServings / recipe.recipeServings
    } else {
        1.0
    }

    // Check if recipe has parsed ingredients (can be scaled)
    val hasParsedIngredients = remember(recipe) {
        recipe.recipeIngredient.any { it.unit != null && it.food != null }
    }

    val nutritionItems = remember(recipe.nutrition) {
        recipe.nutrition?.let { buildNutritionItems(it) }
    }
    val shouldShowNutrition = (recipe.settings?.showNutrition ?: true) && !nutritionItems.isNullOrEmpty()

    BoxWithConstraints(modifier = modifier) {
        // Use a breakpoint of 600dp for tablet/expanded layout
        val isTablet = maxWidth > 600.dp

        if (isTablet) {
            RecipeDetailTwoPane(
                recipe = recipe,
                baseUrl = baseUrl,
                isAdmin = isAdmin,
                onEditCategoriesClick = onEditCategoriesClick,
                onEditTagsClick = onEditTagsClick,
                gatheredIngredients = gatheredIngredients,
                onToggleIngredient = { index ->
                    if (gatheredIngredients.contains(index)) gatheredIngredients.remove(index)
                    else gatheredIngredients.add(index)
                },
                completedInstructions = completedInstructions,
                onToggleInstruction = { index ->
                    if (completedInstructions.contains(index)) completedInstructions.remove(index)
                    else completedInstructions.add(index)
                },
                currentServings = currentServings,
                onServingsChange = { currentServings = it },
                servingsMultiplier = servingsMultiplier,
                hasParsedIngredients = hasParsedIngredients,
                shouldShowNutrition = shouldShowNutrition,
                nutritionItems = nutritionItems
            )
        } else {
            RecipeDetailVertical(
                recipe = recipe,
                baseUrl = baseUrl,
                isAdmin = isAdmin,
                onEditCategoriesClick = onEditCategoriesClick,
                onEditTagsClick = onEditTagsClick,
                gatheredIngredients = gatheredIngredients,
                onToggleIngredient = { index ->
                    if (gatheredIngredients.contains(index)) gatheredIngredients.remove(index)
                    else gatheredIngredients.add(index)
                },
                completedInstructions = completedInstructions,
                onToggleInstruction = { index ->
                    if (completedInstructions.contains(index)) completedInstructions.remove(index)
                    else completedInstructions.add(index)
                },
                currentServings = currentServings,
                onServingsChange = { currentServings = it },
                servingsMultiplier = servingsMultiplier,
                hasParsedIngredients = hasParsedIngredients,
                shouldShowNutrition = shouldShowNutrition,
                nutritionItems = nutritionItems
            )
        }
    }
}

@Composable
fun RecipeDetailVertical(
    recipe: RecipeDetail,
    baseUrl: String,
    isAdmin: Boolean,
    onEditCategoriesClick: () -> Unit,
    onEditTagsClick: () -> Unit,
    gatheredIngredients: Set<Int>,
    onToggleIngredient: (Int) -> Unit,
    completedInstructions: Set<Int>,
    onToggleInstruction: (Int) -> Unit,
    currentServings: Double,
    onServingsChange: (Double) -> Unit,
    servingsMultiplier: Double,
    hasParsedIngredients: Boolean,
    shouldShowNutrition: Boolean,
    nutritionItems: List<Pair<String, String>>?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        RecipeImage(recipe, baseUrl)

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RecipeHeader(recipe)
            RecipeMetadata(recipe, isAdmin, onEditCategoriesClick, onEditTagsClick)
            RecipeInfo(recipe, currentServings, onServingsChange, hasParsedIngredients)
            RecipeIngredients(recipe, gatheredIngredients, servingsMultiplier, onToggleIngredient)
            RecipeInstructions(recipe, completedInstructions, onToggleInstruction)
            RecipeNutrition(shouldShowNutrition, nutritionItems)
            RecipeNotes(recipe)
            RecipeFooter(recipe)
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun RecipeDetailTwoPane(
    recipe: RecipeDetail,
    baseUrl: String,
    isAdmin: Boolean,
    onEditCategoriesClick: () -> Unit,
    onEditTagsClick: () -> Unit,
    gatheredIngredients: Set<Int>,
    onToggleIngredient: (Int) -> Unit,
    completedInstructions: Set<Int>,
    onToggleInstruction: (Int) -> Unit,
    currentServings: Double,
    onServingsChange: (Double) -> Unit,
    servingsMultiplier: Double,
    hasParsedIngredients: Boolean,
    shouldShowNutrition: Boolean,
    nutritionItems: List<Pair<String, String>>?
) {
    Row(modifier = Modifier.fillMaxSize()) {
        // Left Pane: Info, Meta, Image, Ingredients
        Column(
            modifier = Modifier
                .weight(0.4f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            RecipeImage(recipe, baseUrl)
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RecipeHeader(recipe)
                RecipeMetadata(recipe, isAdmin, onEditCategoriesClick, onEditTagsClick)
                RecipeInfo(recipe, currentServings, onServingsChange, hasParsedIngredients)
                RecipeIngredients(recipe, gatheredIngredients, servingsMultiplier, onToggleIngredient)
                RecipeNutrition(shouldShowNutrition, nutritionItems)
                RecipeNotes(recipe)
                RecipeFooter(recipe)
            }
        }

        // Right Pane: Instructions
        Column(
            modifier = Modifier
                .weight(0.6f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .padding(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            RecipeInstructions(recipe, completedInstructions, onToggleInstruction)
        }
    }
}

@Composable
private fun RecipeImage(recipe: RecipeDetail, baseUrl: String) {
    val imageUrl = "$baseUrl/api/media/recipes/${recipe.id}/images/original.webp" +
            if (recipe.updatedAt != null) "?v=${recipe.updatedAt}" else ""
    AsyncImage(
        model = imageUrl,
        contentDescription = recipe.name,
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(horizontal = 16.dp)
            .clip(MaterialTheme.shapes.medium),
        contentScale = ContentScale.Crop
    )
}

@Composable
private fun RecipeHeader(recipe: RecipeDetail) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
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
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecipeMetadata(
    recipe: RecipeDetail,
    isAdmin: Boolean,
    onEditCategoriesClick: () -> Unit,
    onEditTagsClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Categories
        if (isAdmin || !recipe.recipeCategory.isNullOrEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Categories", style = MaterialTheme.typography.titleMedium)
                    if (isAdmin) {
                        IconButton(onClick = onEditCategoriesClick, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Categories",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                if (!recipe.recipeCategory.isNullOrEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy((-6).dp)
                    ) {
                        recipe.recipeCategory?.forEach { category: RecipeCategory ->
                            AssistChip(
                                onClick = { /* Disabled */ },
                                label = { Text(category.name) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    labelColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            )
                        }
                    }
                }
            }
        }

        // Tags
        if (isAdmin || !recipe.tags.isNullOrEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Tags", style = MaterialTheme.typography.titleMedium)
                    if (isAdmin) {
                        IconButton(onClick = onEditTagsClick, modifier = Modifier.size(32.dp)) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Tags",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                if (!recipe.tags.isNullOrEmpty()) {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy((-6).dp)
                    ) {
                        recipe.tags?.forEach { tag: RecipeTag ->
                            AssistChip(
                                onClick = { /* Disabled */ },
                                label = { Text(tag.name) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeInfo(
    recipe: RecipeDetail,
    currentServings: Double,
    onServingsChange: (Double) -> Unit,
    hasParsedIngredients: Boolean
) {
    val hasTimeData = hasTimeData(recipe)
    val servingsDisplay = getServingsDisplay(recipe)
    val hasServingsData = servingsDisplay != null

    when {
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
                    baseServings = recipe.recipeServings,
                    currentServings = currentServings,
                    onServingsChange = onServingsChange,
                    canScale = hasParsedIngredients,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        hasTimeData -> {
            TimeCard(
                recipe = recipe,
                modifier = Modifier.fillMaxWidth()
            )
        }
        hasServingsData -> {
            ServingsCard(
                baseServings = recipe.recipeServings,
                currentServings = currentServings,
                onServingsChange = onServingsChange,
                canScale = hasParsedIngredients,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun RecipeIngredients(
    recipe: RecipeDetail,
    gatheredIngredients: Set<Int>,
    servingsMultiplier: Double,
    onToggleIngredient: (Int) -> Unit
) {
    if (recipe.recipeIngredient.isNotEmpty()) {
        Card(modifier = Modifier.fillMaxWidth()) {
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

                recipe.recipeIngredient.forEachIndexed { index, ingredient ->
                    if (!ingredient.title.isNullOrBlank()) {
                        Text(
                            text = ingredient.title,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                        )
                    } else {
                        val isGathered = gatheredIngredients.contains(index)
                        IngredientItem(
                            ingredient = ingredient,
                            servingsMultiplier = servingsMultiplier,
                            isGathered = isGathered,
                            onToggle = { onToggleIngredient(index) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeInstructions(
    recipe: RecipeDetail,
    completedInstructions: Set<Int>,
    onToggleInstruction: (Int) -> Unit
) {
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
                    onToggle = { onToggleInstruction(index) }
                )
            }
        }
    }
}

@Composable
private fun RecipeNutrition(
    shouldShowNutrition: Boolean,
    nutritionItems: List<Pair<String, String>>?
) {
    if (shouldShowNutrition && nutritionItems != null) {
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Nutrition",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                HorizontalDivider()

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    nutritionItems.forEach { (label, value) ->
                        NutritionValueRow(label = label, value = value)
                    }
                }
            }
        }
    }
}

@Composable
private fun RecipeNotes(recipe: RecipeDetail) {
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
}

@Composable
private fun RecipeFooter(recipe: RecipeDetail) {
    recipe.orgURL?.let { originalUrl ->
        if (originalUrl.isNotBlank()) {
            val context = LocalContext.current
            OutlinedButton(
                onClick = {
                    val intent = Intent(
                        Intent.ACTION_VIEW,
                        originalUrl.toUri()
                    )
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInBrowser,
                    contentDescription = null
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("View Original Recipe")
            }
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

@Composable
private fun NutritionValueRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun buildNutritionItems(nutrition: Nutrition): List<Pair<String, String>> {
    val entries = mutableListOf<Pair<String, String>>()

    fun addEntry(label: String, value: String?, unit: String?) {
        val trimmed = value?.trim().orEmpty()
        if (trimmed.isNotEmpty()) {
            val displayValue = if (unit.isNullOrBlank() || trimmed.any { it.isLetter() }) {
                trimmed
            } else {
                "$trimmed ${unit.trim()}"
            }
            entries.add(label to displayValue)
        }
    }

    addEntry("Calories", nutrition.calories, "kcal")
    addEntry("Fat", nutrition.fatContent, "g")
    addEntry("Saturated Fat", nutrition.saturatedFatContent, "g")
    addEntry("Unsaturated Fat", nutrition.unsaturatedFatContent, "g")
    addEntry("Trans Fat", nutrition.transFatContent, "g")
    addEntry("Protein", nutrition.proteinContent, "g")
    addEntry("Carbohydrates", nutrition.carbohydrateContent, "g")
    addEntry("Fiber", nutrition.fiberContent, "g")
    addEntry("Sugar", nutrition.sugarContent, "g")
    addEntry("Cholesterol", nutrition.cholesterolContent, "mg")
    addEntry("Sodium", nutrition.sodiumContent, "mg")

    return entries
}
