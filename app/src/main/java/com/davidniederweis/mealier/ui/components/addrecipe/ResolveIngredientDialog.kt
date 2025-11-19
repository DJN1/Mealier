package com.davidniederweis.mealier.ui.components.addrecipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.davidniederweis.mealier.ui.components.addrecipe.CreateItemDialog
import com.davidniederweis.mealier.ui.components.addrecipe.FoodDropdown
import com.davidniederweis.mealier.ui.components.addrecipe.UnitDropdown

import com.davidniederweis.mealier.data.model.unit.RecipeUnit
import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.ui.viewmodel.recipe.IngredientInput
import com.davidniederweis.mealier.ui.viewmodel.recipe.IngredientResolution

@Composable
fun ResolveIngredientDialog(
    resolution: IngredientResolution,
    units: List<RecipeUnit>,
    foods: List<Food>,
    onResolve: (IngredientInput) -> Unit,
    onDiscard: () -> Unit,
    onCancel: () -> Unit,
    onCreateUnit: (String) -> Unit,
    onCreateFood: (String) -> Unit
) {
    val ingredient = resolution.parsed.ingredient
    
    // Initialize state with parsed values
    // Use key(resolution) to force recomposition when resolution changes
    // This ensures that when we move to the next item in the queue, the state is reset
    var quantity by remember(resolution) { mutableStateOf(if (ingredient.quantity > 0) ingredient.quantity.toString() else "") }
    
    // Try to match unit and food from parsed result to existing lists
    val initialUnit = remember(resolution, units) {
        ingredient.unit?.let { u -> 
            units.find { it.id == u.id } ?: units.find { it.name.equals(u.name, ignoreCase = true) }
        }
    }
    var selectedUnit by remember(resolution) { mutableStateOf(initialUnit) }
    
    // Food will be null initially since we are resolving it, but we can use parsed name to search/create
    // Or if we have a partial match, maybe we can preselect? 
    // For now, start with null food, but let the dropdown handle the name searching.
    var selectedFood by remember(resolution) { mutableStateOf<Food?>(null) }
    
    // We want to pre-fill the food search text with the parsed food name
    val parsedFoodName = ingredient.food?.name ?: ""
    
    var note by remember(resolution) { mutableStateOf(ingredient.note ?: "") }

    var showUnitDialog by remember { mutableStateOf(false) }
    var showFoodDialog by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDiscard) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Resolve Ingredient",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onCancel) {
                        Icon(Icons.Default.Close, contentDescription = "Cancel parsing")
                    }
                }
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "Original Text:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = resolution.originalInput.originalText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Quantity
                    OutlinedTextField(
                        value = quantity,
                        onValueChange = { quantity = it },
                        label = { Text("Quantity") },
                        placeholder = { Text("1") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    // Unit Dropdown
                    UnitDropdown(
                        selectedUnit = selectedUnit,
                        units = units,
                        onUnitSelected = { selectedUnit = it },
                        onCreateNew = { showUnitDialog = true },
                        modifier = Modifier.weight(1.5f)
                    )
                }

                // Food Dropdown
                // We need a way to pre-fill the search text or show the parsed name as a hint
                // The current FoodDropdown doesn't expose search text control easily unless we modify it.
                // Assuming FoodDropdown manages its own search state.
                // Let's just show the parsed name above it if food is not selected.
                
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (selectedFood == null && parsedFoodName.isNotBlank()) {
                        Text(
                            text = "Parsed Food: $parsedFoodName",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    FoodDropdown(
                        selectedFood = selectedFood,
                        foods = foods,
                        onFoodSelected = { selectedFood = it },
                        onCreateNew = { showFoodDialog = true },
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Note
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDiscard) {
                        Text("Skip")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            onResolve(
                                IngredientInput(
                                    quantity = quantity,
                                    unit = selectedUnit,
                                    food = selectedFood,
                                    note = note,
                                    originalText = resolution.originalInput.originalText,
                                    referenceId = resolution.originalInput.referenceId
                                )
                            )
                        },
                        enabled = selectedFood != null // Require a food selection to resolve
                    ) {
                        Text("Save & Next")
                    }
                }
            }
        }
    }
    
    // Create Unit Dialog
    if (showUnitDialog) {
        CreateItemDialog(
            title = "Create New Unit",
            label = "Unit Name",
            onDismiss = { showUnitDialog = false },
            onConfirm = { name ->
                onCreateUnit(name)
                showUnitDialog = false
            }
        )
    }

    // Create Food Dialog
    if (showFoodDialog) {
        CreateItemDialog(
            title = "Create New Food",
            label = "Food Name",
            initialValue = parsedFoodName, // Pre-fill with parsed name
            onDismiss = { showFoodDialog = false },
            onConfirm = { name ->
                onCreateFood(name)
                showFoodDialog = false
            }
        )
    }
}
