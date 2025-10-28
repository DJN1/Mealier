package com.davidniederweis.mealier.data.repository

class ImportRecipeFromUrlUseCase(private val recipeImportRepository: RecipeImportRepository) {
    suspend operator fun invoke(url: String) = recipeImportRepository.importRecipeFromUrl(url)
}