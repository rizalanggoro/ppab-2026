package com.asprak.todolistd.feature.todo.presentation

import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.feature.todo.presentation.create.CreateTodoUiState
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Unit tests for TodoFormUiState
 * Tests the immutable form state data class
 */
class CreateTodoViewModelTest {

    @Test
    fun todoFormUiState_initialValues_areCorrect() {
        val state = CreateTodoUiState()
        assertEquals("", state.title)
        assertEquals("", state.description)
        assertEquals(null, state.categoryId)
        assertEquals(false, state.isSaving)
        assertEquals(null, state.error)
        assertEquals(null, state.existingTodoId)
        assertEquals(emptyList<Category>(), state.categories)
    }

    @Test
    fun todoFormUiState_copy_withTitle() {
        val initialState = CreateTodoUiState()
        val title = "New Todo"
        val newState = initialState.copy(title = title)

        assertEquals(title, newState.title)
        assertEquals("", newState.description)
    }

    @Test
    fun todoFormUiState_copy_withDescription() {
        val initialState = CreateTodoUiState()
        val description = "Test description"
        val newState = initialState.copy(description = description)

        assertEquals(description, newState.description)
    }

    @Test
    fun todoFormUiState_copy_withCategoryId() {
        val initialState = CreateTodoUiState()
        val categoryId = 5
        val newState = initialState.copy(categoryId = categoryId)

        assertEquals(categoryId, newState.categoryId)
    }

    @Test
    fun todoFormUiState_copy_withCategories() {
        val initialState = CreateTodoUiState()
        val categories = listOf(
            Category(id = 1, name = "Work", ownerEmail = "test@example.com")
        )
        val newState = initialState.copy(categories = categories)

        assertEquals(categories, newState.categories)
    }

    @Test
    fun todoFormUiState_copy_forEditMode() {
        val initialState = CreateTodoUiState()
        val todoId = 42
        val newState = initialState.copy(existingTodoId = todoId)

        assertEquals(todoId, newState.existingTodoId)
    }

    @Test
    fun todoFormUiState_copy_withError() {
        val initialState = CreateTodoUiState()
        val error = "Validation error"
        val newState = initialState.copy(error = error)

        assertEquals(error, newState.error)
    }

    @Test
    fun todoFormUiState_copy_withSavingState() {
        val initialState = CreateTodoUiState()
        val newState = initialState.copy(isSaving = true)

        assertEquals(true, newState.isSaving)
    }
}




