package com.example.eventmanagement.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {

    private const val PREF_NAME = "theme_preferences"
    private const val KEY_THEME = "selected_theme"
    const val SYSTEM = "system"
    const val LIGHT = "light"
    const val DARK = "dark"

    fun applyTheme(context: Context) {
        val preferences = context.getSharedPreferences( PREF_NAME, Context.MODE_PRIVATE )
        when (preferences.getString(KEY_THEME, SYSTEM)) {
            LIGHT -> {
                AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_NO )
            }
            DARK -> {
                AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_YES )
            }
            else -> {
                AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM )
            }
        }
    }

    fun setTheme(context: Context, theme: String ) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME, theme)
            .apply()
        when (theme) {
            LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
            }
            DARK -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
            }
            SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                )
            }
        }
    }
}