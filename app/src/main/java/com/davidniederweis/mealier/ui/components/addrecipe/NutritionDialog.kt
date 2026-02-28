package com.davidniederweis.mealier.ui.components.addrecipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeFormViewModel

@Composable
fun NutritionDialog(
    viewModel: RecipeFormViewModel,
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
                    label = { Text("Calories (kcal)") },
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
                    value = localNutrition.saturatedFatContent,
                    onValueChange = { localNutrition = localNutrition.copy(saturatedFatContent = it) },
                    label = { Text("Saturated Fat (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNutrition.unsaturatedFatContent,
                    onValueChange = { localNutrition = localNutrition.copy(unsaturatedFatContent = it) },
                    label = { Text("Unsaturated Fat (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNutrition.transFatContent,
                    onValueChange = { localNutrition = localNutrition.copy(transFatContent = it) },
                    label = { Text("Trans Fat (g)") },
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
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNutrition.sugarContent,
                    onValueChange = { localNutrition = localNutrition.copy(sugarContent = it) },
                    label = { Text("Sugar (g)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNutrition.cholesterolContent,
                    onValueChange = { localNutrition = localNutrition.copy(cholesterolContent = it) },
                    label = { Text("Cholesterol (mg)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = localNutrition.sodiumContent,
                    onValueChange = { localNutrition = localNutrition.copy(sodiumContent = it) },
                    label = { Text("Sodium (mg)") },
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
