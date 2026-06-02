package com.asprak.todolistd.feature.todo.data

import android.content.Context
import androidx.core.content.edit
import com.asprak.todolistd.domain.Todo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton
import dagger.hilt.android.qualifiers.ApplicationContext

@Singleton
class TodoRepository @Inject constructor(
    @ApplicationContext context: Context,
) {
    companion object {
        private const val PREF_NAME = "todo_prefs"
        private const val KEY_TODOS = "todos"
    }

    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val _todos = MutableStateFlow(loadTodos())
    val todos: StateFlow<List<Todo>> = _todos.asStateFlow()

    fun observeTodos(ownerEmail: String) = todos.map { currentTodos ->
        currentTodos.filter { todo -> todo.ownerEmail.equals(ownerEmail, ignoreCase = true) }
    }

    fun observeTodo(ownerEmail: String, todoId: Int) = todos.map { currentTodos ->
        currentTodos.firstOrNull { todo ->
            todo.ownerEmail.equals(ownerEmail, ignoreCase = true) && todo.id == todoId
        }
    }

    fun create(
        ownerEmail: String,
        title: String,
        description: String,
        categoryId: Int,
    ): Todo {
        val todo = Todo(
            id = nextId(_todos.value),
            title = title.trim(),
            description = description.trim(),
            isDone = false,
            categoryId = categoryId,
            ownerEmail = ownerEmail.lowercase(),
        )

        _todos.value += todo
        persist()
        return todo
    }

    fun update(
        ownerEmail: String,
        todoId: Int,
        title: String,
        description: String,
        categoryId: Int,
    ) {
        _todos.value = _todos.value.map { todo ->
            when {
                todo.ownerEmail.equals(
                    ownerEmail,
                    ignoreCase = true
                ) && todo.id == todoId -> todo.copy(
                    title = title.trim(),
                    description = description.trim(),
                    categoryId = categoryId,
                )

                else -> todo
            }
        }
        persist()
    }

    fun toggleCompletion(ownerEmail: String, todoId: Int) {
        _todos.value = _todos.value.map { todo ->
            when {
                todo.ownerEmail.equals(
                    ownerEmail,
                    ignoreCase = true
                ) && todo.id == todoId -> todo.copy(
                    isDone = !todo.isDone,
                )

                else -> todo
            }
        }
        persist()
    }

    fun delete(ownerEmail: String, todoId: Int) {
        _todos.value = _todos.value.filterNot { todo ->
            todo.ownerEmail.equals(ownerEmail, ignoreCase = true) && todo.id == todoId
        }
        persist()
    }

    fun deleteAllByCategory(ownerEmail: String, categoryId: Int) {
        _todos.value = _todos.value.filterNot { todo ->
            todo.ownerEmail.equals(ownerEmail, ignoreCase = true) && todo.categoryId == categoryId
        }
        persist()
    }

    fun getTodo(ownerEmail: String, todoId: Int): Todo? {
        return _todos.value.firstOrNull { todo ->
            todo.ownerEmail.equals(ownerEmail, ignoreCase = true) && todo.id == todoId
        }
    }

    private fun persist() {
        val raw = json.encodeToString(_todos.value)
        preferences.edit { putString(KEY_TODOS, raw) }
    }

    private fun nextId(items: List<Todo>): Int {
        return (items.maxOfOrNull { it.id } ?: 0) + 1
    }

    private fun loadTodos(): List<Todo> {
        val raw = preferences.getString(KEY_TODOS, null) ?: return emptyList()
        return runCatching {
            json.decodeFromString<List<Todo>>(raw)
        }.getOrDefault(emptyList())
    }
}


