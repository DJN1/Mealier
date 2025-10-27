package com.davidniederweis.mealier.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.davidniederweis.mealier.data.model.cookbook.Cookbook
import com.davidniederweis.mealier.ui.viewmodel.appViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.CookbookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CookbookDropdown(
    selectedCookbook: Cookbook?,
    onCookbookSelected: (Cookbook) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CookbookViewModel = appViewModel()
) {
    val cookbooks by viewModel.cookbooks.collectAsState()
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadCookbooks()
    }

    Box(
        modifier = modifier
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
        ) {
            OutlinedTextField(
                value = selectedCookbook?.name ?: "Select a cookbook",
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                cookbooks.forEach { cookbook ->
                    DropdownMenuItem(
                        text = { Text(text = cookbook.name) },
                        onClick = {
                            onCookbookSelected(cookbook)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
