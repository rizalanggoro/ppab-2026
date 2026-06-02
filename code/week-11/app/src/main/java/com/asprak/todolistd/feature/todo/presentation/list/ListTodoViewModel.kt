package com.asprak.todolistd.feature.todo.presentation.list

import androidx.lifecycle.ViewModel
import com.asprak.todolistd.feature.auth.data.AuthRepository
import com.asprak.todolistd.feature.category.data.CategoryRepository
import com.asprak.todolistd.feature.todo.data.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ListTodoViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val todoRepository: TodoRepository,
    private val categoryRepository: CategoryRepository,
) : ViewModel() {
//    private val ownerEmail = authRepository.session
//        .map { session -> session?.email.orEmpty() }
//        .distinctUntilChanged()
//
//    val uiState = combine(
//        ownerEmail,
//        todoRepository.todos,
//        categoryRepository.categories,
//    ) { email, todos, categories ->
//        val filteredTodos = todos.filter { todo -> todo.ownerEmail.equals(email, ignoreCase = true) }
//        val filteredCategories = categories.filter { category -> category.ownerEmail.equals(email, ignoreCase = true) }
//
//        ListTodoUiState(
//            todos = filteredTodos,
//            categories = filteredCategories,
//        )
//    }.stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(5_000),
//        initialValue = ListTodoUiState(),
//    )
//
//    fun toggleTodoDone(todoId: Int) {
//        val email = authRepository.session.value?.email?.takeIf { it.isNotBlank() } ?: return
//        todoRepository.toggleCompletion(email, todoId)
//    }
//
//    fun logout() {
//        authRepository.logout()
//    }
}

