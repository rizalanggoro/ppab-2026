package com.asprak.todolistd.feature.auth.data

import kotlinx.coroutines.delay
import kotlin.random.Random

class AuthRepository {
    suspend fun login(email: String, password: String) {
        delay(1000L)
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (cleanEmail != "rizaldwianggoro@email.com" || cleanPassword != "password") {
            throw Exception("Alamat email atau kata sandi tidak valid!")
        }
    }

    suspend fun register(name: String, email: String, password: String) {
        delay(1000L)
        if (Random.nextBoolean()) {
            throw Exception("Gagal melakukan registrasi, silakan coba lagi!")
        }
    }
}