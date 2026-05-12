package com.asprak.todolistd.feature.todo.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.asprak.todolistd.core.MyApplication
import com.asprak.todolistd.domain.Todo
import com.asprak.todolistd.feature.todo.data.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ListTodoViewModel(
    private val todoRepository: TodoRepository
) : ViewModel() {
    companion object {
        val Factory = viewModelFactory {
            initializer {
                val app = this[APPLICATION_KEY] as MyApplication
                ListTodoViewModel(
                    todoRepository = app.todoRepository,
                )
            }
        }
    }

    private val _uiState = MutableStateFlow(ListTodoUiState())
    val uiState: StateFlow<ListTodoUiState> = _uiState.asStateFlow()

    fun setTodos(todos: List<Todo>) {
        _uiState.update { it.copy(todos = todos, isLoading = false) }
    }

    fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun setError(error: String?) {
        _uiState.update { it.copy(error = error) }
    }
}

