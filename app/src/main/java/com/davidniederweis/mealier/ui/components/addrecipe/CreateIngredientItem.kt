package com.davidniederweis.mealier.ui.components.addrecipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.model.unit.RecipeUnit
import com.davidniederweis.mealier.ui.viewmodel.recipe.IngredientInput

@Composable
fun CreateIngredientItem(
    ingredient: IngredientInput,
    units: List<RecipeUnit>,
    foods: List<Food>,
    onIngredientChange: (IngredientInput) -> Unit,
    onRemove: () -> Unit,
    canRemove: Boolean,
    onCreateUnit: (String) -> Unit,
    onCreateFood: (String) -> Unit
) {
    var showUnitDialog by remember { mutableStateOf(false) }
    var showFoodDialog by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Quantity
            OutlinedTextField(
                value = ingredient.quantity,
                onValueChange = { onIngredientChange(ingredient.copy(quantity = it)) },
                label = { Text("Quantity") },
                placeholder = { Text("1") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            // Unit Dropdown
            UnitDropdown(
                selectedUnit = ingredient.unit,
                units = units,
                onUnitSelected = { onIngredientChange(ingredient.copy(unit = it)) },
                onCreateNew = { showUnitDialog = true },
                modifier = Modifier.weight(1.5f)
            )

            // Remove button
            if (canRemove) {
                IconButton(onClick = onRemove) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove ingredient",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Food Dropdown
        FoodDropdown(
            selectedFood = ingredient.food,
            foods = foods,
            onFoodSelected = { onIngredientChange(ingredient.copy(food = it)) },
            onCreateNew = { showFoodDialog = true },
            modifier = Modifier.fillMaxWidth()
        )

        // Note
        OutlinedTextField(
            value = ingredient.note,
            onValueChange = { onIngredientChange(ingredient.copy(note = it)) },
            label = { Text("Note (optional)") },
            placeholder = { Text("e.g., diced, chopped") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
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
            onDismiss = { showFoodDialog = false },
            onConfirm = { name ->
                onCreateFood(name)
                showFoodDialog = false
            }
        )
    }
}
