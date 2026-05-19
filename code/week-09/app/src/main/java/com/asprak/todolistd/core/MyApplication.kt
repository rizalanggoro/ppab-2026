package com.asprak.todolistd.core

import android.app.Application
import com.asprak.todolistd.feature.auth.data.AuthRepository
import com.asprak.todolistd.feature.category.data.CategoryRepository
import com.asprak.todolistd.feature.setting.data.ThemeRepository
import com.asprak.todolistd.feature.todo.data.TodoRepository

class MyApplication : Application() {
    lateinit var themeRepository: ThemeRepository
        private set

    lateinit var authRepository: AuthRepository
        private set

    lateinit var todoRepository: TodoRepository
        private set

    lateinit var categoryRepository: CategoryRepository
        private set

    override fun onCreate() {
        super.onCreate()

        themeRepository = ThemeRepository(this)
        authRepository = AuthRepository(this)
        todoRepository = TodoRepository(this)
        categoryRepository = CategoryRepository(
            context = this,
            todoRepository = todoRepository,
        )
    }
}