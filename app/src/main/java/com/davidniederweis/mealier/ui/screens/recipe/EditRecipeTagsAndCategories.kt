package com.davidniederweis.mealier.ui.screens.recipe

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.data.model.recipe.RecipeCategory
import com.davidniederweis.mealier.data.model.recipe.RecipeTag

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditRecipeTagsSheet(
    recipeTags: List<RecipeTag>,
    allTags: List<RecipeTag>,
    onTagsChange: (List<RecipeTag>) -> Unit,
    onDismiss: () -> Unit
) {
    EditRecipeDataSheet(
        title = "Edit Tags",
        label = "Tags",
        selectedItems = recipeTags,
        allItems = allTags,
        onSave = onTagsChange,
        onDismiss = onDismiss,
        itemName = { it.name },
        itemId = { it.id }
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditRecipeCategoriesSheet(
    recipeCategories: List<RecipeCategory>,
    allCategories: List<RecipeCategory>,
    onCategoriesChange: (List<RecipeCategory>) -> Unit,
    onDismiss: () -> Unit
) {
    EditRecipeDataSheet(
        title = "Edit Categories",
        label = "Categories",
        selectedItems = recipeCategories,
        allItems = allCategories,
        onSave = onCategoriesChange,
        onDismiss = onDismiss,
        itemName = { it.name },
        itemId = { it.id }
    )
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun <T> EditRecipeDataSheet(
    title: String,
    label: String,
    selectedItems: List<T>,
    allItems: List<T>,
    onSave: (List<T>) -> Unit,
    onDismiss: () -> Unit,
    itemName: (T) -> String,
    itemId: (T) -> Any?,
) {
    val currentSelectedItems = remember { mutableStateListOf<T>().apply { addAll(selectedItems) } }
    var searchQuery by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .imePadding(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search $label") },
                modifier = Modifier.fillMaxWidth()
            )

            if (currentSelectedItems.isNotEmpty()) {
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    currentSelectedItems.forEach { item ->
                        FilterChip(
                            selected = true,
                            onClick = { currentSelectedItems.remove(item) },
                            label = { Text(itemName(item)) }
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 250.dp)
            ) {
                val filtered = allItems.filter {
                    itemName(it).contains(searchQuery, ignoreCase = true) && !currentSelectedItems.any { selected -> itemId(it) == itemId(selected) }
                }
                items(filtered) { item ->
                    ListItem(
                        headlineContent = { Text(itemName(item)) },
                        modifier = Modifier.clickable { currentSelectedItems.add(item) }
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    onSave(currentSelectedItems.toList())
                    onDismiss()
                }) {
                    Text("Save")
                }
            }
        }
    }
}
