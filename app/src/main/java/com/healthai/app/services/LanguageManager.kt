package com.healthai.app.services

import android.content.Context
import android.content.SharedPreferences

class LanguageManager(context: Context) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("language_prefs", Context.MODE_PRIVATE)

    fun saveLanguage(language: String) {
        with(sharedPreferences.edit()) {
            putString("selected_language", language)
            apply()
        }
    }

    fun getLanguage(): String {
        return sharedPreferences.getString("selected_language", "en") ?: "en" // Default to English
    }
}