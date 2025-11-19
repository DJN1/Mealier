package com.davidniederweis.mealier.ui.components.addrecipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.ui.viewmodel.recipe.IngredientInput
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeFormViewModel

@Composable
fun AddIngredientSection(
    viewModel: RecipeFormViewModel,
    ingredients: List<IngredientInput>
) {
    val units by viewModel.units.collectAsState()
    val foods by viewModel.foods.collectAsState()
    val isParsing by viewModel.isParsing.collectAsState()
    val resolutionQueue by viewModel.resolutionQueue.collectAsState()

    val hasUnparsedIngredients = remember(ingredients) {
        ingredients.any { it.food == null && it.originalText.isNotBlank() }
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Ingredients",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isParsing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    } else if (hasUnparsedIngredients) {
                        TextButton(onClick = { viewModel.parseIngredients() }) {
                            Text("Parse")
                        }
                    }
                    IconButton(onClick = { viewModel.addIngredient() }) {
                        Icon(Icons.Default.Add, contentDescription = "Add ingredient")
                    }
                }
            }

            ingredients.forEachIndexed { index, ingredient ->
                CreateIngredientItem(
                    ingredient = ingredient,
                    units = units,
                    foods = foods,
                    onIngredientChange = { updated ->
                        viewModel.updateIngredient(index, updated)
                    },
                    onRemove = { viewModel.removeIngredient(index) },
                    canRemove = ingredients.size > 1,
                    onCreateUnit = { name ->
                        viewModel.createUnit(name) { newUnit ->
                            viewModel.updateIngredient(
                                index,
                                ingredient.copy(unit = newUnit)
                            )
                        }
                    },
                    onCreateFood = { name ->
                        viewModel.createFood(name) { newFood ->
                            viewModel.updateIngredient(
                                index,
                                ingredient.copy(food = newFood)
                            )
                        }
                    }
                )
                if (index < ingredients.size - 1) {
                    HorizontalDivider()
                }
            }
        }
    }

    // Resolution Dialog
    if (resolutionQueue.isNotEmpty()) {
        val resolution = resolutionQueue.first()
        ResolveIngredientDialog(
            resolution = resolution,
            units = units,
            foods = foods,
            onResolve = { accepted ->
                viewModel.resolveIngredient(resolution, accepted)
            },
            onDiscard = {
                viewModel.discardResolution()
            },
            onCancel = {
                viewModel.cancelParsing()
            },
            onCreateUnit = { name ->
                viewModel.createUnit(name) { /* Unit created, will be available in dropdown */ }
            },
            onCreateFood = { name ->
                viewModel.createFood(name) { /* Food created, will be available in dropdown */ }
            }
        )
    }
}
