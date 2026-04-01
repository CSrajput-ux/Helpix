package com.healthai.app.services

import android.content.Context
import android.content.SharedPreferences

class ThemeManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE)

    companion object {
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
        const val THEME_SYSTEM = "system"
    }

    fun saveTheme(theme: String) {
        prefs.edit().putString("selected_theme", theme).apply()
    }

    fun getTheme(): String {
        return prefs.getString("selected_theme", THEME_SYSTEM) ?: THEME_SYSTEM
    }
}
