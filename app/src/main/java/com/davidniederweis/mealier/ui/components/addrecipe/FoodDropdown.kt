package com.davidniederweis.mealier.ui.components.addrecipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.data.model.food.Food

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodDropdown(
    selectedFood: Food?,
    foods: List<Food>,
    onFoodSelected: (Food) -> Unit,
    onCreateNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Update search query when a food is selected to reset state if needed, 
    // or initialize. 
    // Actually, we probably want the search field to be empty when we open the menu again.

    val filteredFoods = remember(foods, searchQuery) {
        if (searchQuery.isBlank()) {
            foods
        } else {
            foods.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    // Add focusing to handle IME and dropdown visibility
    // When typing, we want to make sure the dropdown doesn't obscure the text field
    // In ExposedDropdownMenuBox, the menu is anchored to the text field.
    // If it covers the text field, it's usually because of insufficient space or window logic.
    // A common workaround/fix for standard Dropdowns is to use a popup property or ensure z-index.
    // But here, if it covers the text field itself, that's odd behavior for ExposedDropdownMenu.
    // It usually appears BELOW the text field.
    // Unless the dialog is small and centering it forces the menu to go up?
    // Let's try ensuring the menu has a max height or adjusting how it's displayed.
    
    // However, user says "dropdown covers everything".
    // Let's try to use `MenuAnchorType.PrimaryEditable` correctly which we are.
    // Maybe we can try `matchTextFieldWidth = true` explicitly if not default?
    
    // Another issue might be that when `expanded` is true, we switch `value` to `searchQuery`.
    // If `searchQuery` is empty initially, the field becomes empty, which is correct for typing.
    
    // Let's try to fix the "covering" issue. 
    // If inside a Dialog, window bounds are tighter.
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = if (expanded) searchQuery else (selectedFood?.name ?: ""),
            onValueChange = { 
                searchQuery = it 
                if (!expanded) expanded = true
            },
            readOnly = false,
            label = { Text("Food *") },
            placeholder = { Text("Select food") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { 
                expanded = false 
                searchQuery = "" 
            },
            modifier = Modifier.heightIn(max = 200.dp) // Limit height to prevent taking over full screen
        ) {
            // Create new
            DropdownMenuItem(
                text = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text("Create new food")
                    }
                },
                onClick = {
                    expanded = false
                    // searchQuery contains the typed text, maybe pass it to create dialog?
                    // But onCreateNew takes no args currently in calling code, though we could modify it.
                    // The dialog itself asks for name again.
                    searchQuery = ""
                    onCreateNew()
                }
            )

            HorizontalDivider()

            // Filtered foods
            filteredFoods.forEach { food ->
                DropdownMenuItem(
                    text = { Text(food.name) },
                    onClick = {
                        onFoodSelected(food)
                        expanded = false
                        searchQuery = ""
                    }
                )
            }
        }
    }
}
