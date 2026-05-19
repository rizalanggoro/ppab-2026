package com.asprak.todolistd.feature.category.presentation

import com.asprak.todolistd.domain.Category

data class CategoryUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: Int? = null,
    val name: String = "",
    val isSaving: Boolean = false,
    val error: String? = null,
)

