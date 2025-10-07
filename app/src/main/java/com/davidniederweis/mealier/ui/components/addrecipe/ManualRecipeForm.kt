package com.davidniederweis.mealier.ui.components.addrecipe

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeCreationState
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeFormViewModel
import java.io.File
import java.io.FileOutputStream

@Composable
fun ManualRecipeForm(
    viewModel: RecipeFormViewModel,
    creationState: RecipeCreationState,
    submitButtonText: String = "Create Recipe",
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    val recipeName by viewModel.recipeName.collectAsState()
    val recipeDescription by viewModel.recipeDescription.collectAsState()
    val ingredients by viewModel.ingredients.collectAsState()
    val instructions by viewModel.instructions.collectAsState()
    val servings by viewModel.servings.collectAsState()
    val prepTime by viewModel.prepTime.collectAsState()
    val cookTime by viewModel.cookTime.collectAsState()
    val totalTime by viewModel.totalTime.collectAsState()
    val imageFile by viewModel.imageFile.collectAsState()
    val imageUrl by viewModel.imageUrl.collectAsState()

    var showImageDialog by remember { mutableStateOf(false) }
    var showNutritionDialog by remember { mutableStateOf(false) }

    // Image picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val file = File(context.cacheDir, "recipe_image_${System.currentTimeMillis()}.jpg")
                inputStream?.use { input ->
                    FileOutputStream(file).use { output ->
                        input.copyTo(output)
                    }
                }
                viewModel.setImageFile(file)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Recipe Name
        OutlinedTextField(
            value = recipeName,
            onValueChange = viewModel::updateRecipeName,
            label = { Text("Recipe Name *") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Description
        OutlinedTextField(
            value = recipeDescription,
            onValueChange = viewModel::updateRecipeDescription,
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        // Image Section
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Image",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                if (imageFile != null) {
                    Text("Image selected: ${imageFile?.name}")
                    TextButton(onClick = { viewModel.setImageFile(null) }) {
                        Text("Remove")
                    }
                } else if (imageUrl.isNotBlank()) {
                    Text("Image URL: $imageUrl")
                    TextButton(onClick = { viewModel.setImageUrl("") }) {
                        Text("Remove")
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { imagePickerLauncher.launch("image/*") },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Upload")
                        }
                        OutlinedButton(
                            onClick = { showImageDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Link, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("URL")
                        }
                    }
                }
            }
        }

        // Recipe Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = servings,
                onValueChange = viewModel::updateServings,
                label = { Text("Servings") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        // Time fields
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = prepTime,
                onValueChange = viewModel::updatePrepTime,
                label = { Text("Prep Time") },
                placeholder = { Text("PT30M") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            OutlinedTextField(
                value = cookTime,
                onValueChange = viewModel::updateCookTime,
                label = { Text("Cook Time") },
                placeholder = { Text("PT1H") },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
        }

        OutlinedTextField(
            value = totalTime,
            onValueChange = viewModel::updateTotalTime,
            label = { Text("Total Time") },
            placeholder = { Text("PT1H30M") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Ingredients Section
        AddIngredientSection(viewModel = viewModel, ingredients = ingredients)

        // Instructions Section
        AddInstructionSection(viewModel = viewModel, instructions = instructions)

        // Nutrition Button
        val nutrition by viewModel.nutrition.collectAsState()
        val hasNutrition = nutrition.hasAnyValue()
        
        OutlinedButton(
            onClick = { showNutritionDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Analytics, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text(if (hasNutrition) "Edit Nutrition Information" else "Add Nutrition Information")
        }

        // Submit Button
        Button(
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
            enabled = creationState !is RecipeCreationState.Loading
        ) {
            if (creationState is RecipeCreationState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
            } else {
                Text(submitButtonText)
            }
        }

        // Error Message
        if (creationState is RecipeCreationState.Error) {
            Text(
                text = creationState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }

    // Image URL Dialog
    if (showImageDialog) {
        ImageUrlDialog(
            onDismiss = { showImageDialog = false },
            onConfirm = { url ->
                viewModel.setImageUrl(url)
                showImageDialog = false
            }
        )
    }

    // Nutrition Dialog
    if (showNutritionDialog) {
        NutritionDialog(
            viewModel = viewModel,
            onDismiss = { showNutritionDialog = false }
        )
    }
}
