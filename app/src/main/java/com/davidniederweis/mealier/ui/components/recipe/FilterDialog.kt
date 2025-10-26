package com.davidniederweis.mealier.ui.components.recipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.davidniederweis.mealier.data.model.category.Category
import com.davidniederweis.mealier.data.model.tag.Tag
import kotlinx.coroutines.delay

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterDialog(
    categories: List<Category>,
    tags: List<Tag>,
    initialSelectedCategoryIds: List<String>,
    initialSelectedTagIds: List<String>,
    onDismiss: () -> Unit,
    onApply: (selectedCategories: List<String>, selectedTags: List<String>) -> Unit
) {
    var selectedCategories by remember {
        mutableStateOf(categories.filter { it.id in initialSelectedCategoryIds })
    }
    var selectedTags by remember {
        mutableStateOf(tags.filter { it.id in initialSelectedTagIds })
    }

    var categorySearch by remember { mutableStateOf("") }
    var tagSearch by remember { mutableStateOf("") }

    val debouncedCategorySearch by produceState(initialValue = "", key1 = categorySearch) {
        delay(1000)
        value = categorySearch
    }

    val debouncedTagSearch by produceState(initialValue = "", key1 = tagSearch) {
        delay(1000)
        value = tagSearch
    }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var tagDropdownExpanded by remember { mutableStateOf(false) }

    var categoryTextFieldWidth by remember { mutableStateOf(0.dp) }
    var tagTextFieldWidth by remember { mutableStateOf(0.dp) }
    val density = LocalDensity.current

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Recipes") },
        text = {
            Column {
                // Category Filter
                Box {
                    TextField(
                        value = categorySearch,
                        onValueChange = {
                            categorySearch = it
                            categoryDropdownExpanded = true
                        },
                        label = { Text("Categories") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { 
                                categoryTextFieldWidth = with(density) { it.size.width.toDp() }
                            }
                            .onFocusChanged { 
                                if (it.isFocused) {
                                    categoryDropdownExpanded = true
                                }
                            }
                    )
                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false },
                        properties = PopupProperties(focusable = false),
                        modifier = Modifier
                            .width(categoryTextFieldWidth)
                            .heightIn(max = 200.dp)
                    ) {
                        categories.filter { it.name.contains(debouncedCategorySearch, ignoreCase = true) }.forEach { category ->
                            DropdownMenuItem(
                                text = { Text(category.name) },
                                onClick = {
                                    categorySearch = ""
                                    if (!selectedCategories.contains(category)) {
                                        selectedCategories = selectedCategories + category
                                    }
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                FlowRow(modifier = Modifier.padding(top = 8.dp)) {
                    selectedCategories.forEach { category ->
                        InputChip(
                            selected = false,
                            onClick = { selectedCategories = selectedCategories - category },
                            label = { Text(category.name) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove Category",
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Tag Filter
                Box {
                    TextField(
                        value = tagSearch,
                        onValueChange = {
                            tagSearch = it
                            tagDropdownExpanded = true
                        },
                        label = { Text("Tags") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned { 
                                tagTextFieldWidth = with(density) { it.size.width.toDp() }
                            }
                            .onFocusChanged { 
                                if (it.isFocused) {
                                    tagDropdownExpanded = true
                                }
                            }
                    )
                    DropdownMenu(
                        expanded = tagDropdownExpanded,
                        onDismissRequest = { tagDropdownExpanded = false },
                        properties = PopupProperties(focusable = false),
                        modifier = Modifier
                            .width(tagTextFieldWidth)
                            .heightIn(max = 200.dp)
                    ) {
                        tags.filter { it.name.contains(debouncedTagSearch, ignoreCase = true) }.forEach { tag ->
                            DropdownMenuItem(
                                text = { Text(tag.name) },
                                onClick = {
                                    tagSearch = ""
                                    if (!selectedTags.contains(tag)) {
                                        selectedTags = selectedTags + tag
                                    }
                                    tagDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
                FlowRow(modifier = Modifier.padding(top = 8.dp)) {
                    selectedTags.forEach { tag ->
                        InputChip(
                            selected = false,
                            onClick = { selectedTags = selectedTags - tag },
                            label = { Text(tag.name) },
                            trailingIcon = {
                                Icon(
                                    Icons.Default.Close,
                                    contentDescription = "Remove Tag",
                                    modifier = Modifier.size(18.dp)
                                )
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onApply(selectedCategories.map { it.id }, selectedTags.map { it.id }) }
            ) {
                Text("Apply")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
