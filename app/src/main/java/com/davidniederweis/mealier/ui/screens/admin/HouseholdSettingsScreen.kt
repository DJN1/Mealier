package com.davidniederweis.mealier.ui.screens.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.davidniederweis.mealier.ui.viewmodel.admin.HouseholdSettingsViewModel
import com.davidniederweis.mealier.ui.viewmodel.appViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HouseholdSettingsScreen(
    navController: NavController,
    viewModel: HouseholdSettingsViewModel = appViewModel()
) {
    var privateHousehold by remember { mutableStateOf(false) }
    var lockRecipeEdits by remember { mutableStateOf(true) }
    var allowUsersOutsideGroup by remember { mutableStateOf(true) }
    var showNutritionInfo by remember { mutableStateOf(true) }
    var showRecipeAssets by remember { mutableStateOf(false) }
    var defaultToLandscape by remember { mutableStateOf(false) }
    var disableCommenting by remember { mutableStateOf(false) }
    var firstDayOfWeek by remember { mutableStateOf("Sunday") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Household Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = {
                    viewModel.updateSettings(
                        privateHousehold = privateHousehold,
                        lockRecipeEdits = lockRecipeEdits,
                        allowUsersOutsideGroup = allowUsersOutsideGroup,
                        showNutritionInfo = showNutritionInfo,
                        showRecipeAssets = showRecipeAssets,
                        defaultToLandscape = defaultToLandscape,
                        disableCommenting = disableCommenting,
                        firstDayOfWeek = firstDayOfWeek
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Update")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Household Preferences", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            SettingItem(
                title = "Private Household",
                description = "Setting your household to private will disable all public view options. This overrides any individual public view settings",
                checked = privateHousehold,
                onCheckedChange = { privateHousehold = it }
            )
            SettingItem(
                title = "Lock recipe edits from other households",
                description = "When enabled only users in your household can edit recipes created by your household",
                checked = lockRecipeEdits,
                onCheckedChange = { lockRecipeEdits = it }
            )
            WeekDropDown(
                selectedDay = firstDayOfWeek,
                onDaySelected = { firstDayOfWeek = it }
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text("Household Recipe Preferences", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            SettingItem(
                title = "Allow users outside of your group to see your recipes",
                description = "When enabled you can use a public share link to share specific recipes without authorizing the user. When disabled, you can only share recipes with users who are in your group or with a pre-generated private link",
                checked = allowUsersOutsideGroup,
                onCheckedChange = { allowUsersOutsideGroup = it }
            )
            SettingItem(
                title = "Show nutrition information",
                description = "When enabled the nutrition information will be shown on the recipe if available. If there is no nutrition information available, the nutrition information will not be shown",
                checked = showNutritionInfo,
                onCheckedChange = { showNutritionInfo = it }
            )
            SettingItem(
                title = "Show recipe assets",
                description = "When enabled the recipe assets will be shown on the recipe if available",
                checked = showRecipeAssets,
                onCheckedChange = { showRecipeAssets = it }
            )
            SettingItem(
                title = "Default to landscape view",
                description = "When enabled the recipe header section will be shown in landscape view",
                checked = defaultToLandscape,
                onCheckedChange = { defaultToLandscape = it }
            )
            SettingItem(
                title = "Disable users from commenting on recipes",
                description = "Hides the comment section on the recipe page and disables commenting",
                checked = disableCommenting,
                onCheckedChange = { disableCommenting = it }
            )
        }
    }
}

@Composable
fun SettingItem(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(checked = checked, onCheckedChange = onCheckedChange)
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            Text(description, style = MaterialTheme.typography.bodySmall)
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekDropDown(
    selectedDay: String,
    onDaySelected: (String) -> Unit
) {
    val options = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
    ) {
        TextField(
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true),
            readOnly = true,
            value = selectedDay,
            onValueChange = {},
            label = { Text("First day of the week") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onDaySelected(selectionOption)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
