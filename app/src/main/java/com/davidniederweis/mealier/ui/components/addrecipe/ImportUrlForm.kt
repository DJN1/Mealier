package com.davidniederweis.mealier.ui.components.addrecipe

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.davidniederweis.mealier.ui.viewmodel.recipe.AddRecipeViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipe.RecipeCreationState

@Composable
fun ImportUrlForm(
    viewModel: AddRecipeViewModel,
    creationState: RecipeCreationState
) {
    val recipeUrl by viewModel.recipeUrl.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(32.dp))

        Text(
            text = "Import Recipe from URL",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Paste a recipe URL from any website and we'll automatically import it.",
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = recipeUrl,
            onValueChange = viewModel::updateRecipeUrl,
            label = { Text("Recipe URL") },
            placeholder = { Text("https://example.com/recipe") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            enabled = creationState !is RecipeCreationState.Loading
        )

        Button(
            onClick = { viewModel.createRecipeFromUrl() },
            modifier = Modifier.fillMaxWidth(),
            enabled = creationState !is RecipeCreationState.Loading && recipeUrl.isNotBlank()
        ) {
            if (creationState is RecipeCreationState.Loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary
                )
                Spacer(Modifier.width(8.dp))
                Text("Importing...")
            } else {
                Text("Import Recipe")
            }
        }

        if (creationState is RecipeCreationState.Error) {
            Text(
                text = creationState.message,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}
