package com.davidniederweis.mealier.ui.screens.recipe

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

    val filteredFoods = remember(foods, searchQuery) {
        if (searchQuery.isBlank()) {
            foods
        } else {
            foods.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedFood?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Food *") },
            placeholder = { Text("Select food") },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                singleLine = true
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

            HorizontalDivider()

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
                    searchQuery = ""
                    onCreateNew()
                }
            )
        }
    }
}
