package com.asprak.todolistd.domain

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val id: Int,
    val name: String,
    val ownerEmail: String
)
