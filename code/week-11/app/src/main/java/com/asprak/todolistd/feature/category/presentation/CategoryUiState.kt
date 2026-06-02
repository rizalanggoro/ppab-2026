package com.asprak.todolistd.feature.category.presentation

import com.asprak.todolistd.dto.DtoCategory

data class CategoryUiState(
    val isLoading: Boolean = false,
    val categories: List<DtoCategory> = emptyList(),
    val name: String = "",
    val isCreating: Boolean = false,
    val isDeletingId: Int? = null,
)

