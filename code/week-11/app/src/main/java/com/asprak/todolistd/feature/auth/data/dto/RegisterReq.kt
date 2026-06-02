package com.asprak.todolistd.feature.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReq(
    val name: String,
    val email: String,
    val password: String
)
