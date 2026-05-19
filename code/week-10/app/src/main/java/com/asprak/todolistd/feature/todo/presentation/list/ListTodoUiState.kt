package com.asprak.todolistd.feature.todo.presentation.list

import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.domain.Todo

data class ListTodoUiState(
    val todos: List<Todo> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

