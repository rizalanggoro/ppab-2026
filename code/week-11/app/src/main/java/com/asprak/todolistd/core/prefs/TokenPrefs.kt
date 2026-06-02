package com.asprak.todolistd.core.prefs

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenPrefs @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val sharedPreferences = context.getSharedPreferences(
        "token_prefs",
        Context.MODE_PRIVATE
    )

    private var _token = MutableStateFlow(get())
    val token = _token.asStateFlow()

    fun set(token: String) {
        sharedPreferences.edit {
            putString(
                "auth_token",
                token
            )
        }

        _token.update {
            token
        }
    }

    private fun get(): String? {
        return sharedPreferences.getString(
            "auth_token",
            null
        )
    }
}