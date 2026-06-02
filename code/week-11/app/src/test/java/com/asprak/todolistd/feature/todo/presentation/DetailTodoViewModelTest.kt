package com.asprak.todolistd.feature.todo.presentation

import com.asprak.todolistd.domain.Todo
import com.asprak.todolistd.feature.todo.presentation.detail.DetailTodoUiState
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for DetailTodoUiState
 * Tests the immutable detail view state data class
 */
class DetailTodoViewModelTest {

    @Test
    fun detailTodoUiState_initialValues_areCorrect() {
        val state = DetailTodoUiState()
        assertEquals(null, state.todo)
        assertEquals(null, state.categoryName)
        assertEquals(false, state.isLoading)
        assertEquals(null, state.error)
        assertEquals(false, state.isDeleting)
    }

    @Test
    fun detailTodoUiState_copy_withTodo() {
        val initialState = DetailTodoUiState()
        val mockTodo = Todo(
            id = 1,
            title = "Test Todo",
            description = "Test Description",
            isDone = false,
            categoryId = 1,
            ownerEmail = "test@example.com"
        )

        val newState = initialState.copy(todo = mockTodo, isLoading = false)

        assertEquals(mockTodo, newState.todo)
        assertEquals(false, newState.isLoading)
    }

    @Test
    fun detailTodoUiState_copy_withCategoryName() {
        val initialState = DetailTodoUiState()
        val categoryName = "Work"
        val newState = initialState.copy(categoryName = categoryName)

        assertEquals(categoryName, newState.categoryName)
    }

    @Test
    fun detailTodoUiState_copy_withLoading() {
        val initialState = DetailTodoUiState()
        val newState = initialState.copy(isLoading = true)

        assertEquals(true, newState.isLoading)
    }

    @Test
    fun detailTodoUiState_copy_withError() {
        val initialState = DetailTodoUiState()
        val errorMessage = "Test error"
        val newState = initialState.copy(error = errorMessage)

        assertEquals(errorMessage, newState.error)
    }

    @Test
    fun detailTodoUiState_copy_withDeleting() {
        val initialState = DetailTodoUiState()
        val newState = initialState.copy(isDeleting = true)

        assertEquals(true, newState.isDeleting)
    }

    @Test
    fun detailTodoUiState_copy_multipleFields() {
        val initialState = DetailTodoUiState()
        val mockTodo = Todo(
            id = 42,
            title = "Important Task",
            description = "Complete project",
            isDone = true,
            categoryId = 2,
            ownerEmail = "user@example.com"
        )

        val newState = initialState.copy(
            todo = mockTodo,
            categoryName = "Personal",
            isLoading = false,
            error = null,
            isDeleting = false
        )

        assertEquals(mockTodo, newState.todo)
        assertEquals("Personal", newState.categoryName)
        assertEquals(false, newState.isLoading)
        assertEquals(null, newState.error)
        assertEquals(false, newState.isDeleting)
    }
}




