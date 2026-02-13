package com.healthai.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.navigation.compose.rememberNavController
import com.healthai.app.services.LanguageManager
import com.healthai.app.ui.navigation.AppNavGraph
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setLocale()

        setContent {
            androidx.compose.material3.MaterialTheme { 
                val navController = rememberNavController()
                AppNavGraph(navController = navController)
            }
        }
    }

    private fun setLocale() {
        val languageManager = LanguageManager(this)
        val lang = languageManager.getLanguage()
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }
}