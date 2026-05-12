package com.asprak.todolistd.feature.auth.data

import android.content.Context
import androidx.core.content.edit
import com.asprak.todolistd.domain.Session
import com.asprak.todolistd.domain.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json

class AuthRepository(
    context: Context,
) {
    companion object {
        private const val PREF_NAME = "auth_prefs"
        private const val KEY_USERS = "users"
        private const val KEY_SESSION_EMAIL = "session_email"
    }

    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val _users = MutableStateFlow(loadUsers())
    val users = _users.asStateFlow()

    private val _session = MutableStateFlow(loadSession())
    val session = _session.asStateFlow()

    fun register(name: String, email: String, password: String) {
        val normalizedEmail = email.trim().lowercase()

        if (name.isBlank() || normalizedEmail.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Semua field wajib diisi!")
        }

        if (users.value.any { it.email.equals(normalizedEmail, ignoreCase = true) }) {
            throw IllegalStateException("Email sudah terdaftar!")
        }

        val newUser = User(
            name = name.trim(),
            email = normalizedEmail,
            password = password,
        )

        val updatedUsers = _users.value + newUser
        _users.value = updatedUsers
        saveUsers(updatedUsers)
        setSession(normalizedEmail)
    }

    fun login(email: String, password: String) {
        val normalizedEmail = email.trim().lowercase()

        if (normalizedEmail.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Email dan kata sandi wajib diisi!")
        }

        val user = users.value.firstOrNull { candidate ->
            candidate.email.equals(
                normalizedEmail,
                ignoreCase = true
            ) && candidate.password == password
        } ?: throw IllegalStateException("Alamat email atau kata sandi tidak valid!")

        setSession(user.email)
    }

    fun logout() {
        _session.value = null
        preferences.edit { remove(KEY_SESSION_EMAIL) }
    }

    private fun setSession(email: String) {
        _session.value = Session(email)
        preferences.edit { putString(KEY_SESSION_EMAIL, email) }
    }

    private fun loadUsers(): List<User> {
        val raw = preferences.getString(KEY_USERS, null) ?: return emptyList()
        return runCatching {
            json.decodeFromString<List<User>>(raw)
        }.getOrDefault(emptyList())
    }

    private fun saveUsers(users: List<User>) {
        val raw = json.encodeToString(users)
        preferences.edit { putString(KEY_USERS, raw) }
    }

    private fun loadSession(): Session? {
        val email = preferences.getString(KEY_SESSION_EMAIL, null) ?: return null
        return Session(email)
    }
}