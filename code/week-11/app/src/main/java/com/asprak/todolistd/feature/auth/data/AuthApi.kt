package com.asprak.todolistd.feature.auth.data

import com.asprak.todolistd.feature.auth.data.dto.LoginReq
import com.asprak.todolistd.feature.auth.data.dto.LoginRes
import com.asprak.todolistd.feature.auth.data.dto.RegisterReq
import com.asprak.todolistd.feature.auth.data.dto.RegisterRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/api/auth/login")
    suspend fun login(
        @Body body: LoginReq
    ): Response<LoginRes>

    @POST("/api/auth/register")
    suspend fun register(
        @Body body: RegisterReq
    ): Response<RegisterRes>
}