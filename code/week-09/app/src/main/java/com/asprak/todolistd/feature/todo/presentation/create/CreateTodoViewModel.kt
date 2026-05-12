package com.asprak.todolistd.feature.todo.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asprak.todolistd.core.MyApplication
import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.feature.category.data.CategoryRepository
import com.asprak.todolistd.feature.todo.data.TodoRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateTodoViewModel(
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                CreateTodoViewModel(
                    todoRepository = app.todoRepository,
                    categoryRepository = app.categoryRepository
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(CreateTodoUiState())
    val uiState = _uiState.asStateFlow()

    private val _successEvent = MutableSharedFlow<Unit>()
    val successEvent = _successEvent.asSharedFlow()

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun setTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    fun setDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun setCategoryId(categoryId: Int?) {
        _uiState.update { it.copy(categoryId = categoryId) }
    }

    fun setCategories(categories: List<Category>) {
        _uiState.update { it.copy(categories = categories) }
    }

    fun setExistingTodoId(todoId: Int?) {
        _uiState.update { it.copy(existingTodoId = todoId) }
    }

    fun saveTodo(ownerEmail: String) = viewModelScope.launch {
        val state = _uiState.value
        if (state.title.isBlank() || state.categoryId == null) {
            _uiState.update { it.copy(error = "Title dan kategori harus diisi") }
            return@launch
        }

        _uiState.update { it.copy(isSaving = true, error = null) }
        try {
            if (state.existingTodoId == null) {
                todoRepository.create(
                    ownerEmail = ownerEmail,
                    title = state.title,
                    description = state.description,
                    categoryId = state.categoryId
                )
            } else {
                todoRepository.update(
                    ownerEmail = ownerEmail,
                    todoId = state.existingTodoId,
                    title = state.title,
                    description = state.description,
                    categoryId = state.categoryId
                )
            }
            _uiState.update { it.copy(isSaving = false) }
            _successEvent.emit(Unit)
            _navigationEvent.emit(Unit)
        } catch (e: Exception) {
            _uiState.update { it.copy(isSaving = false, error = e.message) }
        }
    }
}


