package com.davidniederweis.mealier.ui.screens.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.data.model.unit.RecipeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UnitDropdown(
    selectedUnit: RecipeUnit?,
    units: List<RecipeUnit>,
    onUnitSelected: (RecipeUnit?) -> Unit,
    onCreateNew: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedUnit?.name ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Unit") },
            placeholder = { Text("Select unit") },
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
            // None option
            DropdownMenuItem(
                text = { Text("None") },
                onClick = {
                    onUnitSelected(null)
                    expanded = false
                }
            )

            HorizontalDivider()

            // Existing units
            units.forEach { unit ->
                DropdownMenuItem(
                    text = { Text(unit.name) },
                    onClick = {
                        onUnitSelected(unit)
                        expanded = false
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
                        Text("Create new unit")
                    }
                },
                onClick = {
                    expanded = false
                    onCreateNew()
                }
            )
        }
    }
}
