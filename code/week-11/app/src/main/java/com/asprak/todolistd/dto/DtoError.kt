package com.asprak.todolistd.dto

import kotlinx.serialization.Serializable

@Serializable
data class DtoError(
    val code: Int,
    val message: String
)