package com.asprak.todolistd.feature.todo.presentation.create

import com.asprak.todolistd.domain.Category

data class CreateTodoUiState(
    val title: String = "",
    val description: String = "",
    val categoryId: Int? = null,
    val isSaving: Boolean = false,
    val error: String? = null,
    val existingTodoId: Int? = null,
    val categories: List<Category> = emptyList(),
)