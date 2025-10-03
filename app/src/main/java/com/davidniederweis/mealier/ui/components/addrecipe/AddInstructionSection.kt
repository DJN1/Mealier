package com.davidniederweis.mealier.ui.components.addrecipe

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.ui.viewmodel.recipe.InstructionInput
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeFormViewModel

@Composable
fun AddInstructionSection(
    viewModel: RecipeFormViewModel,
    instructions: List<InstructionInput>
) {
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
                    text = "Instructions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { viewModel.addInstruction() }) {
                    Icon(Icons.Default.Add, contentDescription = "Add instruction")
                }
            }

            instructions.forEachIndexed { index, instruction ->
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Step ${index + 1}",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (instructions.size > 1) {
                            IconButton(onClick = { viewModel.removeInstruction(index) }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Remove step",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = instruction.title,
                        onValueChange = { newTitle ->
                            viewModel.updateInstruction(
                                index,
                                instruction.copy(title = newTitle)
                            )
                        },
                        label = { Text("Title (optional)") },
                        placeholder = { Text("e.g., Prepare ingredients") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = instruction.text,
                        onValueChange = { newText ->
                            viewModel.updateInstruction(
                                index,
                                instruction.copy(text = newText)
                            )
                        },
                        label = { Text("Instructions") },
                        placeholder = { Text("Describe this step") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 3,
                        maxLines = 5
                    )
                }

                if (index < instructions.size - 1) {
                    HorizontalDivider()
                }
            }
        }
    }
}
