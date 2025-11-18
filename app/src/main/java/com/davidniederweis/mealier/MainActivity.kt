package com.davidniederweis.mealier

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.davidniederweis.mealier.ui.MealierApp
import com.davidniederweis.mealier.ui.theme.MealierTheme
import com.davidniederweis.mealier.ui.viewmodel.preferences.ThemeViewModel
import com.davidniederweis.mealier.ui.viewmodel.recipeimport.ImportState
import com.davidniederweis.mealier.ui.viewmodel.recipeimport.RecipeImportViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var recipeImportViewModel: RecipeImportViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge()

        val application = application as MealierApplication
        recipeImportViewModel = ViewModelProvider(this, application.viewModelFactory)[RecipeImportViewModel::class.java]

        setContent {
            val themeViewModel: ThemeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                factory = application.viewModelFactory
            )
            val isDarkMode by themeViewModel.isDarkMode.collectAsState()

            MealierTheme(
                darkTheme = isDarkMode,
                dynamicColor = true
            ) {
                MealierApp()
            }
        }

        handleIntent(intent)

        lifecycleScope.launch {
            recipeImportViewModel.importState.collect { state ->
                when (state) {
                    is ImportState.Success -> {
                        Toast.makeText(this@MainActivity, "Recipe imported successfully!", Toast.LENGTH_SHORT).show()
                    }
                    is ImportState.Error -> {
                        Toast.makeText(this@MainActivity, "Failed to import recipe: ${state.message}", Toast.LENGTH_LONG).show()
                    }
                    else -> { /* Do nothing for Idle or Loading states */ }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
                recipeImportViewModel.importRecipe(it)
            }
        }
    }
}