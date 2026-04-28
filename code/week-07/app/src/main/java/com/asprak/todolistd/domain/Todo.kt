package com.asprak.todolistd.domain

data class Todo(
    val id: Int,
    val title: String,
    val description: String,
    val isDone: Boolean,
    val category: Category?
)