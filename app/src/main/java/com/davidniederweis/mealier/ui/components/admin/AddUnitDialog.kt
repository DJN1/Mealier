package com.davidniederweis.mealier.ui.components.admin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.davidniederweis.mealier.data.model.unit.RecipeUnit

@Composable
fun AddUnitDialog(
    unit: RecipeUnit?,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(unit?.name ?: "") }
    var abbreviation by remember { mutableStateOf(unit?.abbreviation ?: "") }
    var namePlural by remember { mutableStateOf(unit?.pluralName ?: "") }
    var abbreviationPlural by remember { mutableStateOf(unit?.pluralAbbreviation ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(text = if (unit == null) "Add Unit" else "Edit Unit", style = MaterialTheme.typography.titleLarge)

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = namePlural,
                    onValueChange = { namePlural = it },
                    label = { Text("Plural Name") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = abbreviation,
                    onValueChange = { abbreviation = it },
                    label = { Text("Abbreviation") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = abbreviationPlural,
                    onValueChange = { abbreviationPlural = it },
                    label = { Text("Plural Abbreviation") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { onDismiss() },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = { onSave(name, namePlural, abbreviation, abbreviationPlural) }
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}
