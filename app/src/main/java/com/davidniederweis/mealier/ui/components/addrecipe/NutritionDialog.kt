package com.davidniederweis.mealier.ui.screens.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.ui.viewmodel.recipe.AddRecipeViewModel

@Composable
fun NutritionDialog(
    viewModel: AddRecipeViewModel,
    onDismiss: () -> Unit
) {
    val nutrition by viewModel.nutrition.collectAsState()
    var localNutrition by remember { mutableStateOf(nutrition) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nutrition Information") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "All fields are optional",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = localNutrition.calories,
                    onValueChange = { localNutrition = localNutrition.copy(calories = it) },
                    label = { Text("Calories") },
                    placeholder = { Text("200") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNutrition.fatContent,
                    onValueChange = { localNutrition = localNutrition.copy(fatContent = it) },
                    label = { Text("Fat (g)") },
                    placeholder = { Text("10") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNutrition.proteinContent,
                    onValueChange = { localNutrition = localNutrition.copy(proteinContent = it) },
                    label = { Text("Protein (g)") },
                    placeholder = { Text("15") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNutrition.carbohydrateContent,
                    onValueChange = { localNutrition = localNutrition.copy(carbohydrateContent = it) },
                    label = { Text("Carbohydrates (g)") },
                    placeholder = { Text("30") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNutrition.fiberContent,
                    onValueChange = { localNutrition = localNutrition.copy(fiberContent = it) },
                    label = { Text("Fiber (g)") },
                    placeholder = { Text("5") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNutrition.sugarContent,
                    onValueChange = { localNutrition = localNutrition.copy(sugarContent = it) },
                    label = { Text("Sugar (g)") },
                    placeholder = { Text("8") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNutrition.sodiumContent,
                    onValueChange = { localNutrition = localNutrition.copy(sodiumContent = it) },
                    label = { Text("Sodium (mg)") },
                    placeholder = { Text("500") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    viewModel.updateNutrition(localNutrition)
                    onDismiss()
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
