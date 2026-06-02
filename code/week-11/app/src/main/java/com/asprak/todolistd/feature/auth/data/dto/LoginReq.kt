package com.asprak.todolistd.feature.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginBody(
    val email: String,
    val password: String
)
