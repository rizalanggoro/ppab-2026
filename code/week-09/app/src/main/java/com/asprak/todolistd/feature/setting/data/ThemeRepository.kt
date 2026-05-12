package com.asprak.todolistd.feature.setting.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ThemeRepository {
    private val _isDark = MutableStateFlow(true)
    val isDark = _isDark.asStateFlow()

    fun toggleTheme() = _isDark.update { !it }
}