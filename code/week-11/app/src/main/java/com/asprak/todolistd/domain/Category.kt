package com.asprak.todolistd.domain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    @SerialName("user_id")
    val userId: Int,
    val name: String,
)
