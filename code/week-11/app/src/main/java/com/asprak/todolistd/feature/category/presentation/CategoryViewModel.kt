package com.asprak.todolistd.feature.category.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.feature.auth.data.AuthRepository
import com.asprak.todolistd.feature.category.data.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CategoryUiState())
    val uiState = _uiState.asStateFlow()

    fun setName(name: String) = _uiState.update {
        it.copy(name = name)
    }

    fun create() = viewModelScope.launch {
        val name = _uiState.value.name.trim()
        if (name.isBlank()) return@launch

        runCatching {
            _uiState.update {
                it.copy(isCreating = true)
            }

            categoryRepository.create(
                name = name
            )
        }.onSuccess {
            getAllCategories()
        }.also {
            _uiState.update {
                it.copy(isCreating = false)
            }
        }
    }

    fun getAllCategories() = viewModelScope.launch {
        runCatching {
            _uiState.update {
                it.copy(isLoading = true)
            }

            categoryRepository.getAll()
        }.onSuccess { categories ->
            _uiState.update {
                it.copy(categories = categories)
            }
        }.onFailure {
            it.printStackTrace()
        }.also {
            _uiState.update {
                it.copy(isLoading = false)
            }
        }
    }

    fun deleteCategory(category: Category) = viewModelScope.launch {
        runCatching {
            _uiState.update {
                it.copy(isDeletingId = category.id)
            }

            categoryRepository.delete(
                categoryId = category.id
            )
        }.onSuccess {
            getAllCategories()
        }.also {
            _uiState.update {
                it.copy(isDeletingId = null)
            }
        }
    }

    init {
        getAllCategories()
    }
}


