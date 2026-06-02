package com.asprak.todolistd.feature.auth.data

import com.asprak.todolistd.core.extension.errorMessage
import com.asprak.todolistd.core.prefs.TokenPrefs
import com.asprak.todolistd.feature.auth.data.dto.LoginReq
import com.asprak.todolistd.feature.auth.data.dto.RegisterReq
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val tokenPrefs: TokenPrefs,
    private val authApi: AuthApi
) {
    suspend fun register(name: String, email: String, password: String) {
        val response = authApi.register(
            body = RegisterReq(
                name = name,
                email = email,
                password = password
            )
        )

        if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

        val body = response.body() ?: throw IllegalStateException("Respon tidak valid!")
        tokenPrefs.set(token = body.token)
    }

    suspend fun login(email: String, password: String) {
        val response = authApi.login(
            body = LoginReq(
                email = email,
                password = password
            )
        )

        if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

        val body = response.body() ?: throw IllegalStateException("Respon tidak valid!")
        tokenPrefs.set(token = body.token)
    }
}