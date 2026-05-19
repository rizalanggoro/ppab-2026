package com.asprak.todolistd.feature.todo.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.asprak.todolistd.feature.auth.data.AuthRepository
import com.asprak.todolistd.feature.category.data.CategoryRepository
import com.asprak.todolistd.feature.todo.data.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("unused")
@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class CreateTodoViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
    private val _formState = MutableStateFlow(CreateTodoUiState())

    private val ownerEmail = authRepository.session
        .map { session -> session?.email.orEmpty() }
        .distinctUntilChanged()

    private val categories = ownerEmail.flatMapLatest { email ->
        when {
            email.isBlank() -> flowOf(emptyList())
            else -> categoryRepository.observeCategories(email)
        }
    }

    val uiState = combine(_formState, categories) { state, categories ->
        state.copy(categories = categories)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = CreateTodoUiState(),
    )

    private val _navigationEvent = MutableSharedFlow<Unit>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun setTitle(title: String) {
        _formState.update { it.copy(title = title) }
    }

    fun setDescription(description: String) {
        _formState.update { it.copy(description = description) }
    }

    fun setCategoryId(categoryId: Int?) {
        _formState.update { it.copy(categoryId = categoryId) }
    }

    fun loadTodo(todoId: Int?) {
        _formState.update { it.copy(existingTodoId = todoId) }

        if (todoId == null) {
            return
        }

        val email = authRepository.session.value?.email?.takeIf { it.isNotBlank() } ?: return
        todoRepository.getTodo(email, todoId)?.let { todo ->
            _formState.update {
                it.copy(
                    title = todo.title,
                    description = todo.description,
                    categoryId = todo.categoryId,
                    existingTodoId = todoId,
                    error = null,
                )
            }
        }
    }

    fun saveTodo() = viewModelScope.launch {
        val state = _formState.value
        val email = authRepository.session.value?.email?.takeIf { it.isNotBlank() }

        if (state.title.isBlank() || state.categoryId == null) {
            _formState.update { it.copy(error = "Title dan kategori harus diisi") }
            return@launch
        }

        if (email == null) {
            _formState.update { it.copy(error = "Sesi pengguna tidak ditemukan") }
            return@launch
        }

        _formState.update { it.copy(isSaving = true, error = null) }
        try {
            if (state.existingTodoId == null) {
                todoRepository.create(
                    ownerEmail = email,
                    title = state.title,
                    description = state.description,
                    categoryId = state.categoryId
                )
            } else {
                todoRepository.update(
                    ownerEmail = email,
                    todoId = state.existingTodoId,
                    title = state.title,
                    description = state.description,
                    categoryId = state.categoryId
                )
            }
            _formState.update { it.copy(isSaving = false) }
            _navigationEvent.emit(Unit)
        } catch (e: Exception) {
            _formState.update { it.copy(isSaving = false, error = e.message) }
        }
    }
}


