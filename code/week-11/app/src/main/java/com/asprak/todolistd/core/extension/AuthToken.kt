package com.asprak.todolistd.core.extension

import com.asprak.todolistd.core.prefs.TokenPrefs

fun TokenPrefs.bearerToken(): String {
    val token = this.token.value ?: throw IllegalStateException("Token is not available")
    return "Bearer $token"
}