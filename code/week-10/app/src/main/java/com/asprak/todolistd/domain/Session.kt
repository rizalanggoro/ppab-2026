package com.asprak.todolistd.domain

import kotlinx.serialization.Serializable

@Serializable
data class Session(
    val email: String,
)