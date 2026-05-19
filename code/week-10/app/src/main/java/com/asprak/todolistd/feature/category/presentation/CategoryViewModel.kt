package com.asprak.todolistd.feature.category.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.feature.auth.data.AuthRepository
import com.asprak.todolistd.feature.category.data.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
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

    private val ownerEmail = authRepository.session
        .map { session -> session?.email.orEmpty() }
        .distinctUntilChanged()

    val uiState = combine(_uiState, ownerEmail, categoryRepository.categories) { state, email, categories ->
        val filteredCategories = categories.filter { category ->
            category.ownerEmail.equals(email, ignoreCase = true)
        }
        state.copy(categories = filteredCategories)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CategoryUiState(),
    )

    fun setName(name: String) {
        _uiState.update { it.copy(name = name, error = null) }
    }

    fun selectCategory(category: Category) {
        _uiState.update {
            it.copy(
                selectedCategoryId = category.id,
                name = category.name,
                error = null,
            )
        }
    }

    fun clearSelection() {
        _uiState.update {
            it.copy(
                selectedCategoryId = null,
                name = "",
                error = null,
            )
        }
    }

    fun saveCategory() = viewModelScope.launch {
        val state = _uiState.value
        val email = authRepository.session.value?.email?.takeIf { it.isNotBlank() }
            ?: run {
                _uiState.update { it.copy(error = "Sesi pengguna tidak ditemukan") }
                return@launch
            }

        if (state.name.isBlank()) {
            _uiState.update { it.copy(error = "Nama kategori tidak boleh kosong") }
            return@launch
        }

        _uiState.update { it.copy(isSaving = true, error = null) }
        try {
            if (state.selectedCategoryId == null) {
                categoryRepository.create(email, state.name)
            } else {
                categoryRepository.update(email, state.selectedCategoryId, state.name)
            }
            _uiState.update {
                it.copy(
                    isSaving = false,
                    selectedCategoryId = null,
                    name = "",
                    error = null,
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(isSaving = false, error = e.message) }
        }
    }

    fun deleteCategory(category: Category) {
        val email = authRepository.session.value?.email?.takeIf { it.isNotBlank() } ?: return
        categoryRepository.delete(email, category.id)

        if (_uiState.value.selectedCategoryId == category.id) {
            clearSelection()
        }
    }
}


