package com.asprak.todolistd.feature.todo.presentation

import com.asprak.todolistd.domain.Todo
import com.asprak.todolistd.feature.todo.presentation.list.ListTodoUiState
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for ListTodoUiState
 * Tests the immutable state data class
 */
class ListTodoViewModelTest {

    @Test
    fun listTodoUiState_initialValues_areCorrect() {
        val state = ListTodoUiState()
        assertEquals(emptyList<Todo>(), state.todos)
        assertEquals(false, state.isLoading)
        assertEquals(null, state.error)
    }

    @Test
    fun listTodoUiState_copy_withTodos() {
        val initialState = ListTodoUiState()
        val mockTodos = listOf(
            Todo(
                id = 1,
                title = "Test Todo",
                description = "Test Description",
                isDone = false,
                categoryId = 1,
                ownerEmail = "test@example.com"
            )
        )

        val newState = initialState.copy(todos = mockTodos, isLoading = false)

        assertEquals(mockTodos, newState.todos)
        assertEquals(false, newState.isLoading)
        assertEquals(null, newState.error)
    }

    @Test
    fun listTodoUiState_copy_withLoading() {
        val initialState = ListTodoUiState()
        val newState = initialState.copy(isLoading = true)

        assertEquals(true, newState.isLoading)
        assertEquals(emptyList<Todo>(), newState.todos)
    }

    @Test
    fun listTodoUiState_copy_withError() {
        val initialState = ListTodoUiState()
        val errorMessage = "Test error"
        val newState = initialState.copy(error = errorMessage)

        assertEquals(errorMessage, newState.error)
    }
}




