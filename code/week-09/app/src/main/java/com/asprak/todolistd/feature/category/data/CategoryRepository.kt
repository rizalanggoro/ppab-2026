package com.asprak.todolistd.feature.category.data

import android.content.Context
import androidx.core.content.edit
import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.feature.todo.data.TodoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class CategoryRepository(
    context: Context,
    private val todoRepository: TodoRepository,
) {
    companion object {
        private const val PREF_NAME = "category_prefs"
        private const val KEY_CATEGORIES = "categories"
    }

    private val preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    private val _categories = MutableStateFlow(loadCategories())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    fun observeCategories(ownerEmail: String) = categories.map { currentCategories ->
        currentCategories.filter { category ->
            category.ownerEmail.equals(
                ownerEmail,
                ignoreCase = true
            )
        }
    }

    fun create(ownerEmail: String, name: String): Category {
        val category = Category(
            id = nextId(_categories.value),
            name = name.trim(),
            ownerEmail = ownerEmail.lowercase(),
        )

        _categories.value += category
        persist()
        return category
    }

    fun update(ownerEmail: String, categoryId: Int, name: String) {
        _categories.value = _categories.value.map { category ->
            when {
                category.ownerEmail.equals(
                    ownerEmail,
                    ignoreCase = true
                ) && category.id == categoryId -> category.copy(
                    name = name.trim(),
                )

                else -> category
            }
        }
        persist()
    }

    fun delete(ownerEmail: String, categoryId: Int) {
        _categories.value = _categories.value.filterNot { category ->
            category.ownerEmail.equals(ownerEmail, ignoreCase = true) && category.id == categoryId
        }
        persist()
        todoRepository.deleteAllByCategory(ownerEmail, categoryId)
    }

    fun getCategory(ownerEmail: String, categoryId: Int): Category? {
        return _categories.value.firstOrNull { category ->
            category.ownerEmail.equals(ownerEmail, ignoreCase = true) && category.id == categoryId
        }
    }

    private fun persist() {
        val raw = json.encodeToString(_categories.value)
        preferences.edit { putString(KEY_CATEGORIES, raw) }
    }

    private fun nextId(items: List<Category>): Int {
        return (items.maxOfOrNull { it.id } ?: 0) + 1
    }

    private fun loadCategories(): List<Category> {
        val raw = preferences.getString(KEY_CATEGORIES, null) ?: return emptyList()
        return runCatching {
            json.decodeFromString<List<Category>>(raw)
        }.getOrDefault(emptyList())
    }
}


