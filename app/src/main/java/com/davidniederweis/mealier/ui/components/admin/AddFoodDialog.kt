package com.davidniederweis.mealier.ui.components.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.davidniederweis.mealier.data.model.food.Food

@Composable
fun AddFoodDialog(
    food: Food?,
    onDismiss: () -> Unit,
    onSave: (Food) -> Unit
) {
    var name by remember { mutableStateOf(food?.name ?: "") }
    var pluralName by remember { mutableStateOf(food?.pluralName ?: "") }
    var description by remember { mutableStateOf(food?.description ?: "") }
    var onHand by remember { mutableStateOf(food?.onHand ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = if (food == null) "Add Food" else "Edit Food", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = pluralName ?: "",
                    onValueChange = { pluralName = it },
                    label = { Text("Plural Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description ?: "",
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )

                // TODO: Add Food Label dropdown. Requires data model update.
                OutlinedTextField(
                    value = "",
                    onValueChange = { },
                    label = { Text("Food Label") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = false
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = onHand, onCheckedChange = { onHand = it })
                    Text("On Hand")
                }
                Text(
                    text = "Setting this flag will make this food unchecked by default when adding a recipe to a shopping list.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            val updatedFood = Food(
                                id = food?.id,
                                name = name,
                                pluralName = pluralName,
                                description = description,
                                onHand = onHand,
                                label = null // TODO: Get from dropdown
                            )
                            onSave(updatedFood)
                        }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
