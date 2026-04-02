package com.healthai.app.ui.screens.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SettingsSuggest
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.LocaleListCompat
import androidx.navigation.NavController
import com.healthai.app.R
import com.healthai.app.services.LanguageManager
import com.healthai.app.services.ThemeManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val languageManager = remember { LanguageManager(context) }
    val themeManager = remember { ThemeManager(context) }

    Scaffold(
        containerColor = Color(0xFF0B1221),
        topBar = {
            TopAppBar(
                title = { Text("App Settings", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0B1221))
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            SettingsSectionTitle("Personalization")
            
            LanguageSwitcher(languageManager = languageManager, context = context)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ThemeSwitcher(themeManager = themeManager)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            SettingsSectionTitle("About App")
            Text("Version 1.0.0", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        color = colorResource(id = R.color.logo_cyan),
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSwitcher(languageManager: LanguageManager, context: Context){
    val languages = listOf("English", "हिंदी")
    var expanded by remember { mutableStateOf(false) }
    var selectedOptionText by remember { mutableStateOf(if(languageManager.getLanguage() == "hi") "हिंदी" else "English") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOptionText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Language") },
            leadingIcon = { Icon(Icons.Default.Language, contentDescription = null, tint = colorResource(id = R.color.logo_cyan)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.logo_cyan),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                unfocusedLabelColor = Color.Gray,
                focusedLabelColor = colorResource(id = R.color.logo_cyan)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1E293B))
        ) {
            languages.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, color = Color.White) },
                    onClick = {
                        selectedOptionText = selectionOption
                        val lang = if (selectionOption == "English") "en" else "hi"
                        languageManager.saveLanguage(lang)
                        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(lang)
                        AppCompatDelegate.setApplicationLocales(appLocale)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSwitcher(themeManager: ThemeManager){
    val themes = mapOf(
        "Light" to ThemeManager.THEME_LIGHT,
        "Dark" to ThemeManager.THEME_DARK,
        "System Default" to ThemeManager.THEME_SYSTEM
    )
    var expanded by remember { mutableStateOf(false) }
    
    val currentTheme = themeManager.getTheme()
    var selectedOptionText by remember { 
        mutableStateOf(themes.entries.find { it.value == currentTheme }?.key ?: "System Default") 
    }

    val themeIcon = when(currentTheme) {
        ThemeManager.THEME_LIGHT -> Icons.Default.LightMode
        ThemeManager.THEME_DARK -> Icons.Default.DarkMode
        else -> Icons.Default.SettingsSuggest
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedOptionText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Theme") },
            leadingIcon = { Icon(themeIcon, contentDescription = null, tint = colorResource(id = R.color.logo_cyan)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = colorResource(id = R.color.logo_cyan),
                unfocusedBorderColor = Color.Gray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                unfocusedLabelColor = Color.Gray,
                focusedLabelColor = colorResource(id = R.color.logo_cyan)
            )
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1E293B))
        ) {
            themes.keys.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption, color = Color.White) },
                    onClick = {
                        selectedOptionText = selectionOption
                        val themeValue = themes[selectionOption] ?: ThemeManager.THEME_SYSTEM
                        themeManager.saveTheme(themeValue)
                        
                        // Apply theme immediately
                        val mode = when(themeValue) {
                            ThemeManager.THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                            ThemeManager.THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
                            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                        }
                        AppCompatDelegate.setDefaultNightMode(mode)
                        
                        expanded = false
                    }
                )
            }
        }
    }
}
