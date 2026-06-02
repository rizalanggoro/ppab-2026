package com.asprak.todolistd.feature.auth.data

import com.asprak.todolistd.feature.auth.data.dto.LoginRes
import com.asprak.todolistd.feature.auth.data.dto.RegisterRes
import retrofit2.Response
import retrofit2.http.POST

interface AuthApiService {
    @POST("/api/auth/login")
    suspend fun login(
        email: String,
        password: String
    ): Response<LoginRes>

    @POST("/api/auth/register")
    suspend fun register(
        name: String,
        email: String,
        password: String
    ): Response<RegisterRes>
}