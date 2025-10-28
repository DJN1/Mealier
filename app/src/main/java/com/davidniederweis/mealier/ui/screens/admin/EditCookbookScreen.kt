package com.davidniederweis.mealier.ui.screens.admin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.davidniederweis.mealier.ui.viewmodel.admin.EditCookbookViewModel
import com.davidniederweis.mealier.ui.viewmodel.appViewModel

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EditCookbookScreen(
    navController: NavController,
    cookbookId: String,
    viewModel: EditCookbookViewModel = appViewModel()
) {
    val filterList = remember { mutableStateListOf<FilterState>() } // Initialize as empty
    var cookbookName by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }

    val cookbookState by viewModel.cookbook.collectAsState()
    val cookbookSaved by viewModel.cookbookSaved.collectAsState()
    val vmFilters by viewModel.filters.collectAsState() // Observe filters from ViewModel

    LaunchedEffect(cookbookId) {
        viewModel.loadCookbook(cookbookId)
        viewModel.loadInitialData()
    }

    val categories by viewModel.categories.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
    val tools by viewModel.tools.collectAsState()
    val households by viewModel.households.collectAsState()

    // Update filterList when vmFilters changes
    LaunchedEffect(vmFilters, categories, tags, ingredients, tools, households) {
        val allLookupDataLoaded = categories.isNotEmpty() &&
                                   tags.isNotEmpty() &&
                                   ingredients.isNotEmpty() &&
                                   tools.isNotEmpty() &&
                                   households.isNotEmpty()

        filterList.clear() // Clear existing filters

        if (vmFilters.isNotEmpty()) {
            val resolvedFilters = vmFilters.map { filter ->
                val resolvedName = if (allLookupDataLoaded) {
                    when (filter.selectedCategory) {
                        "Categories" -> categories.find { it.id == filter.selectedValue.first }?.name
                        "Tags" -> tags.find { it.id == filter.selectedValue.first }?.name
                        "Ingredients" -> ingredients.find { it.id == filter.selectedValue.first }?.name
                        "Tools" -> tools.find { it.id == filter.selectedValue.first }?.name
                        "Households" -> households.find { it.id == filter.selectedValue.first }?.name
                        // Date categories already handle their own display value
                        else -> filter.selectedValue.second // Fallback to current second value if not a lookup category
                    }
                } else {
                    filter.selectedValue.first // Use ID as display value if lookup data not loaded yet
                }
                // Ensure resolvedName is not null; fallback to ID if lookup somehow failed
                filter.copy(selectedValue = filter.selectedValue.first to (resolvedName ?: filter.selectedValue.first))
            }
            filterList.addAll(resolvedFilters)
        } else if (filterList.isEmpty()) {
            // If vmFilters is empty (e.g., new cookbook or no filters saved) and filterList is also empty, add one default filter
            filterList.add(FilterState())
        }
    }

    LaunchedEffect(cookbookState) {
        cookbookState?.let {
            cookbookName = it.name
            description = it.description
            isPublic = it.public
        }
    }

    LaunchedEffect(cookbookSaved) {
        if (cookbookSaved) {
            navController.popBackStack()
        }
    }

    val lazyListState = rememberLazyListState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit a Cookbook") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) {
        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = cookbookName,
                    onValueChange = { newName -> cookbookName = newName },
                    label = { Text("Cookbook Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { newDescription -> description = newDescription },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            itemsIndexed(filterList, key = { _, item -> item.id }) {
                    index,
                    filter ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .animateItem()
                ) {
                    FilterField(
                        modifier = Modifier,
                        filterState = filter,
                        onDelete = {
                            if (filterList.size > 1) {
                                filterList.removeAt(index)
                            }
                        },
                        onFilterChanged = { newFilterState ->
                            filterList[index] = newFilterState
                        },
                        currentIndex = index,
                        totalItems = filterList.size,
                        onMoveUp = {
                            if (index > 0) {
                                filterList.move(index, index - 1)
                            }
                        },
                        onMoveDown = {
                            if (index < filterList.size - 1) {
                                filterList.move(index, index + 1)
                            }
                        },
                        categories = categories,
                        tags = tags,
                        ingredients = ingredients,
                        tools = tools,
                        households = households
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(onClick = { filterList.add(FilterState()) }) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Field")
                        Text("Add Field")
                    }
                    Spacer(modifier = Modifier.weight(1f)) // Added Spacer with weight to push the next item to the right
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Public Cookbook") // Moved Text before Switch
                        Spacer(modifier = Modifier.width(4.dp)) // Added Spacer for spacing
                        Switch(
                            checked = isPublic,
                            onCheckedChange = { newIsPublic -> isPublic = newIsPublic }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            viewModel.updateCookbook(
                                cookbookId = cookbookId,
                                cookbookName = cookbookName,
                                description = description,
                                isPublic = isPublic,
                                filters = filterList.toList()
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Update")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}