package com.davidniederweis.mealier.ui.components.recipe

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.davidniederweis.mealier.BuildConfig
import com.davidniederweis.mealier.data.model.recipe.RecipeSummary
import com.davidniederweis.mealier.util.Logger

@Composable
fun RecipeCard(
    recipe: RecipeSummary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Always construct the standard image URL with cache-busting parameter
    val imageUrl = remember(recipe.id, recipe.dateUpdated) {
        val baseUrl = "${BuildConfig.BASE_URL}/api/media/recipes/${recipe.id}/images/original.webp"
        val url = if (recipe.dateUpdated != null) "$baseUrl?v=${recipe.dateUpdated}" else baseUrl
        Logger.logImageLoad(url, recipe.id, "images/original.webp")
        url
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp) // Fixed card height
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Recipe Image - Fixed height
            SubcomposeAsyncImage(
                model = imageUrl,
                contentDescription = recipe.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop,
                onSuccess = {
                    Logger.logImageSuccess(imageUrl)
                },
                onError = { error ->
                    Logger.logImageError(imageUrl, error.result.throwable)
                },
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                error = {
                    // Show placeholder on 404 or any error
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Restaurant,
                            contentDescription = "No image available",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )

            // Recipe Info - Fixed height section (100dp total = 280dp - 180dp image)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Recipe Title - takes up to 2 lines
                Text(
                    text = recipe.name ?: "Untitled Recipe",
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )

                // Recipe Description - fills remaining space or shows nothing
                recipe.description?.let { description ->
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f, fill = false) // Takes available space but doesn't force expansion
                    )
                } ?: Spacer(modifier = Modifier.weight(1f)) // Empty space if no description
            }
        }
    }
}
