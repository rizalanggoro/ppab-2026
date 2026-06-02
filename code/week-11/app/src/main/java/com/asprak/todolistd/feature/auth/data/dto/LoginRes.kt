package com.asprak.todolistd.feature.auth.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class LoginRes(
    val token: String
)
