package com.asprak.todolistd.feature.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginReq(
    val email: String,
    val password: String
)
