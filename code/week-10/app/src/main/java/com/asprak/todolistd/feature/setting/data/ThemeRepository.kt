package com.asprak.todolistd.feature.setting.data

import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class ThemeRepository @Inject constructor(
    @ApplicationContext context: Context,
) {
    companion object {
        private const val PREF_NAME = "theme_prefs"
        private const val KEY_IS_DARK = "is_dark"
    }

    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val _isDark = MutableStateFlow(loadTheme())
    val isDark = _isDark.asStateFlow()

    fun toggleTheme() {
        _isDark.update { !it }
        saveTheme(_isDark.value)
    }

    private fun loadTheme(): Boolean {
        return preferences.getBoolean(KEY_IS_DARK, true)
    }

    private fun saveTheme(isDark: Boolean) {
        preferences.edit { putBoolean(KEY_IS_DARK, isDark) }
    }
}