package com.asprak.todolistd.feature.todo.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asprak.todolistd.core.MyApplication
import com.asprak.todolistd.domain.Todo
import com.asprak.todolistd.feature.category.data.CategoryRepository
import com.asprak.todolistd.feature.todo.data.TodoRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DetailTodoViewModel(
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                DetailTodoViewModel(
                    todoRepository = app.todoRepository,
                    categoryRepository = app.categoryRepository
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(DetailTodoUiState())
    val uiState = _uiState.asStateFlow()

    private val _deleteEvent = MutableSharedFlow<Unit>()
    val deleteEvent = _deleteEvent.asSharedFlow()

    fun setTodo(todo: Todo?) {
        _uiState.update { it.copy(todo = todo, isLoading = false) }
    }

    fun setCategoryName(categoryName: String?) {
        _uiState.update { it.copy(categoryName = categoryName) }
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun setError(error: String?) {
        _uiState.update { it.copy(error = error) }
    }

    fun deleteTodo(ownerEmail: String) = viewModelScope.launch {
        val todo = _uiState.value.todo ?: return@launch
        _uiState.update { it.copy(isDeleting = true) }
        try {
            todoRepository.delete(ownerEmail, todo.id)
            _uiState.update { it.copy(isDeleting = false) }
            _deleteEvent.emit(Unit)
        } catch (e: Exception) {
            _uiState.update { it.copy(isDeleting = false, error = e.message) }
        }
    }

    fun toggleTodoDone(ownerEmail: String) {
        val todo = _uiState.value.todo ?: return
        todoRepository.toggleCompletion(ownerEmail, todo.id)
        _uiState.update { it.copy(todo = todo.copy(isDone = !todo.isDone)) }
    }
}

