package com.asprak.todolistd.feature.auth.data

import kotlinx.coroutines.delay
import kotlin.random.Random

class AuthRepository {
    suspend fun login(email: String, password: String) {
        delay(2000L)
        if (email != "rizaldwianggoro@email.com" && password != "password") {
            throw Exception("Alamat email atau kata sandi tidak valid!")
        }
    }

    suspend fun register(name: String, email: String, password: String) {
        delay(2000L)
        if (Random.Default.nextBoolean()) {
            throw Exception("Gagal melakukan registrasi, silakan coba lagi!")
        }
    }
}