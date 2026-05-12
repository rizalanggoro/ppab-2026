package com.asprak.todolistd.feature.todo.presentation.detail

import com.asprak.todolistd.domain.Todo

data class DetailTodoUiState(
    val todo: Todo? = null,
    val categoryName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDeleting: Boolean = false,
)

