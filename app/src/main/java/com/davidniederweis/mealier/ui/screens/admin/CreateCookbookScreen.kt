@file:OptIn(ExperimentalFoundationApi::class)

package com.davidniederweis.mealier.ui.screens.admin

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.PopupProperties
import androidx.navigation.NavController
import com.davidniederweis.mealier.data.model.category.Category
import com.davidniederweis.mealier.data.model.food.Food
import com.davidniederweis.mealier.data.model.household.Household
import com.davidniederweis.mealier.data.model.tag.Tag
import com.davidniederweis.mealier.data.model.tool.Tool
import com.davidniederweis.mealier.ui.viewmodel.admin.CreateCookbookViewModel
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.UUID


data class FilterState(
    val id: UUID = UUID.randomUUID(),
    var selectedCategory: String = "Categories",
    var selectedOperator: String = "is one of",
    var selectedValue: Pair<String, String> = "" to "",
)

// Extension function to move an item in a mutable list
fun <T> SnapshotStateList<T>.move(from: Int, to: Int) {
    if (from == to || from !in 0 until size || to !in 0 until size) return
    val item = this.removeAt(from)
    this.add(to, item)
}

@OptIn(ExperimentalFoundationApi::class,ExperimentalMaterial3Api::class)
@Composable
fun CreateCookbookScreen(
    navController: NavController,
    viewModel: CreateCookbookViewModel = appViewModel()
) {
    val filterList = remember { mutableStateListOf(FilterState()) }
    var cookbookName by remember { mutableStateOf("Family Cookbook 1") }
    var description by remember { mutableStateOf("") }
    var isPublic by remember { mutableStateOf(false) }

    val cookbookSaved by viewModel.cookbookSaved.collectAsState()

    LaunchedEffect(cookbookSaved) {
        if (cookbookSaved) {
            navController.popBackStack()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadInitialData()
    }

    val categories by viewModel.categories.collectAsState()
    val tags by viewModel.tags.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
    val tools by viewModel.tools.collectAsState()
    val households by viewModel.households.collectAsState()

    val lazyListState = rememberLazyListState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Create a Cookbook") },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        )

        LazyColumn(
            state = lazyListState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp)
        ) {
            item {
                OutlinedTextField(
                    value = cookbookName,
                    onValueChange = { cookbookName = it },
                    label = { Text("Cookbook Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
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
                        onFilterChanged = {
                            filterList[index] = it
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
                            onCheckedChange = { isPublic = it }
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
                            viewModel.saveCookbook(
                                cookbookName = cookbookName,
                                description = description,
                                isPublic = isPublic,
                                filters = filterList.toList()
                            )
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Save")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterField(
    modifier: Modifier = Modifier,
    filterState: FilterState,
    onDelete: () -> Unit,
    onFilterChanged: (FilterState) -> Unit,
    currentIndex: Int,
    totalItems: Int,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    categories: List<Category>,
    tags: List<Tag>,
    ingredients: List<Food>,
    tools: List<Tool>,
    households: List<Household>
) {
    val filterCategories = listOf("Categories", "Tags", "Ingredients", "Tools", "Households", "Date Created", "Date Updated")
    val operators = listOf("is one of", "is not one of", "contains all of")

    var showDatePicker by remember { mutableStateOf(false) }

    val valueOptions = when (filterState.selectedCategory) {
        "Categories" -> categories.map { it.id to it.name }
        "Tags" -> tags.map { it.id to it.name }
        "Ingredients" -> ingredients.map { it.id to it.name }
        "Tools" -> tools.map { it.id to it.name }
        "Households" -> households.map { it.id to it.name }
        else -> emptyList()
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = ButtonDefaults.outlinedButtonBorder(enabled = true),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                 Column { // Changed Row to Column for vertical arrangement
                    IconButton(
                        onClick = onMoveUp,
                        enabled = currentIndex > 0 // Disable if it's the first item
                    ) {
                        Icon(imageVector = Icons.Default.KeyboardArrowUp, contentDescription = "Move Up")
                    }
                    IconButton(
                        onClick = onMoveDown,
                        enabled = currentIndex < totalItems - 1 // Disable if it's the last item
                    ) {
                        Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = "Move Down")
                    }
                }
            }
            Column(modifier = Modifier.weight(1f).padding(horizontal = 8.dp)) {
                SearchableDropdown(
                    label = "Category",
                    options = filterCategories.map { it to it },
                    selectedOption = filterState.selectedCategory to filterState.selectedCategory,
                    onOptionSelected = {
                        onFilterChanged(filterState.copy(selectedCategory = it.first, selectedValue = "" to ""))
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SearchableDropdown(
                    label = "Operator",
                    options = operators.map { it to it },
                    selectedOption = filterState.selectedOperator to filterState.selectedOperator,
                    onOptionSelected = { onFilterChanged(filterState.copy(selectedOperator = it.first)) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (filterState.selectedCategory == "Date Created" || filterState.selectedCategory == "Date Updated") {
                    Box(modifier = Modifier.clickable { showDatePicker = true }) {
                        OutlinedTextField(
                            value = filterState.selectedValue.second,
                            onValueChange = {},
                            label = { Text(filterState.selectedCategory) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        )
                    }
                } else {
                    @Suppress("UNCHECKED_CAST")
                    SearchableDropdown(
                        label = "Value",
                        options = valueOptions as List<Pair<String, String>>,
                        selectedOption = filterState.selectedValue,
                        onOptionSelected = { onFilterChanged(filterState.copy(selectedValue = it)) }
                    )
                }
            }

            Row {
                 IconButton(onClick = onDelete) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedDateMillis = datePickerState.selectedDateMillis
                        if (selectedDateMillis != null) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            sdf.timeZone = TimeZone.getTimeZone("UTC")
                            onFilterChanged(filterState.copy(selectedValue = sdf.format(selectedDateMillis) to sdf.format(selectedDateMillis)))
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchableDropdown(
    label: String,
    options: List<Pair<String, String>>,
    selectedOption: Pair<String, String>,
    onOptionSelected: (Pair<String, String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val focusRequester = remember { FocusRequester() }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            readOnly = true,
            value = selectedOption.second,
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.width(with(LocalDensity.current) { textFieldSize.width.toDp() }),
            properties = PopupProperties(focusable = true)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .focusRequester(focusRequester),
                label = { Text("Search") }
            )
            
            LaunchedEffect(expanded) {
                if (expanded) {
                    focusRequester.requestFocus()
                }
            }

            val scrollState = rememberScrollState()
            Column(modifier = Modifier.heightIn(max = 150.dp).verticalScroll(scrollState)) {
                val filteredOptions = options.filter { it.second.contains(searchQuery, ignoreCase = true) }
                filteredOptions.forEach { selectionOption ->
                    DropdownMenuItem(
                        text = { Text(selectionOption.second) },
                        onClick = {
                            onOptionSelected(selectionOption)
                            expanded = false
                            searchQuery = ""
                        }
                    )
                }
            }
        }
    }
}
