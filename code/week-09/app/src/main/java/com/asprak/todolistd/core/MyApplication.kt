package com.asprak.todolistd.core

import android.app.Application
import com.asprak.todolistd.feature.auth.data.AuthRepository
import com.asprak.todolistd.feature.category.data.CategoryRepository
import com.asprak.todolistd.feature.setting.data.ThemeRepository
import com.asprak.todolistd.feature.todo.data.TodoRepository
import com.asprak.todolistd.feature.test.data.CounterRepository
import com.asprak.todolistd.feature.test.data.TickHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MyApplication : Application() {
    val themeRepository = ThemeRepository()
    lateinit var authRepository: AuthRepository
        private set

    lateinit var todoRepository: TodoRepository
        private set

    lateinit var categoryRepository: CategoryRepository
        private set

    lateinit var tickHandler: TickHandler
        private set

    lateinit var counterRepository: CounterRepository
        private set

    override fun onCreate() {
        super.onCreate()

        authRepository = AuthRepository(this)
        todoRepository = TodoRepository(this)
        categoryRepository = CategoryRepository(
            context = this,
            todoRepository = todoRepository,
        )

        tickHandler = TickHandler(
            externalScope = CoroutineScope(SupervisorJob() + Dispatchers.Default),
        )

        counterRepository = CounterRepository(
            tickHandler = tickHandler,
        )
    }
}