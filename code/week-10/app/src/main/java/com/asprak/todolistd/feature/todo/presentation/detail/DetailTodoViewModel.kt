package com.asprak.todolistd.feature.todo.presentation.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asprak.todolistd.feature.auth.data.AuthRepository
import com.asprak.todolistd.feature.category.data.CategoryRepository
import com.asprak.todolistd.feature.todo.data.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
@HiltViewModel
class DetailTodoViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetailTodoUiState())

    private val ownerEmail = authRepository.session
        .map { session -> session?.email.orEmpty() }
        .distinctUntilChanged()

    val uiState = combine(
        _uiState,
        ownerEmail,
        categoryRepository.categories,
    ) { state, email, categories ->
        val categoryName = state.todo?.let { todo ->
            categories.firstOrNull { category ->
                category.ownerEmail.equals(email, ignoreCase = true) && category.id == todo.categoryId
            }?.name
        }

        state.copy(
            categoryName = categoryName,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DetailTodoUiState(),
    )

    fun loadTodo(todoId: Int) {
        val email = authRepository.session.value?.email?.takeIf { it.isNotBlank() } ?: run {
            _uiState.update { it.copy(todo = null, categoryName = null, isLoading = false, error = "Sesi pengguna tidak ditemukan") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }
        val todo = todoRepository.getTodo(email, todoId)
        _uiState.update { it.copy(todo = todo, isLoading = false) }
    }

    private val _deleteEvent = MutableSharedFlow<Unit>()
    val deleteEvent = _deleteEvent.asSharedFlow()

    fun deleteTodo() = viewModelScope.launch {
        val todo = _uiState.value.todo ?: return@launch
        val email = authRepository.session.value?.email?.takeIf { it.isNotBlank() } ?: return@launch
        _uiState.update { it.copy(isDeleting = true) }
        try {
            todoRepository.delete(email, todo.id)
            _uiState.update { it.copy(isDeleting = false) }
            _deleteEvent.emit(Unit)
        } catch (e: Exception) {
            _uiState.update { it.copy(isDeleting = false, error = e.message) }
        }
    }

    fun toggleTodoDone() = viewModelScope.launch {
        val todo = _uiState.value.todo ?: return@launch
        val email = authRepository.session.value?.email?.takeIf { it.isNotBlank() } ?: return@launch
        todoRepository.toggleCompletion(email, todo.id)
        _uiState.update { it.copy(todo = todo.copy(isDone = !todo.isDone)) }
    }
}

