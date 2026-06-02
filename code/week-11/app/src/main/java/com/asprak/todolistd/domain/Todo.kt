package com.asprak.todolistd.domain

import kotlinx.serialization.Serializable

@Serializable
data class Todo(
    val id: Int,
    val title: String,
    val description: String,
    val isDone: Boolean,
    val categoryId: Int,
    val ownerEmail: String
)