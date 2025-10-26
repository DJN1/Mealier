package com.davidniederweis.mealier.ui.screens.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.data.model.recipe.RecipeCategory
import com.davidniederweis.mealier.data.model.recipe.RecipeTag

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditRecipeTagsAndCategoriesSheet(
    recipeTags: List<RecipeTag>,
    recipeCategories: List<RecipeCategory>,
    allTags: List<RecipeTag>,
    allCategories: List<RecipeCategory>,
    onTagsChange: (List<RecipeTag>) -> Unit,
    onCategoriesChange: (List<RecipeCategory>) -> Unit,
    onDismiss: () -> Unit
) {
    val selectedTags = remember { mutableStateListOf<RecipeTag>().apply { addAll(recipeTags) } }
    val selectedCategories = remember { mutableStateListOf<RecipeCategory>().apply { addAll(recipeCategories) } }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxHeight(0.8f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Edit Tags and Categories", style = MaterialTheme.typography.headlineSmall)

            // Categories Section
            Text("Categories", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(allCategories.size) { index ->
                    val category = allCategories[index]
                    val isChecked = selectedCategories.contains(category)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(category.name)
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                if (isChecked) {
                                    selectedCategories.remove(category)
                                } else {
                                    selectedCategories.add(category)
                                }
                            }
                        )
                    }
                }
            }

            // Tags Section
            Text("Tags", style = MaterialTheme.typography.titleMedium)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(allTags.size) { index ->
                    val tag = allTags[index]
                    val isChecked = selectedTags.contains(tag)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(tag.name)
                        Checkbox(
                            checked = isChecked,
                            onCheckedChange = {
                                if (isChecked) {
                                    selectedTags.remove(tag)
                                } else {
                                    selectedTags.add(tag)
                                }
                            }
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    onTagsChange(selectedTags)
                    onCategoriesChange(selectedCategories)
                    onDismiss()
                }) {
                    Text("Save")
                }
            }
        }
    }
}
