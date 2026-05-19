package com.asprak.todolistd.core

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.asprak.todolistd.feature.auth.data.AuthRepository
import com.asprak.todolistd.feature.auth.presentation.AuthScreen
import com.asprak.todolistd.feature.category.presentation.CategoryScreen
import com.asprak.todolistd.feature.setting.data.ThemeRepository
import com.asprak.todolistd.feature.todo.presentation.create.CreateTodoScreen
import com.asprak.todolistd.feature.todo.presentation.detail.DetailTodoScreen
import com.asprak.todolistd.feature.todo.presentation.list.ListTodoScreen
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun ComposeApp(
    themeRepository: ThemeRepository,
    authRepository: AuthRepository,
) {
    val isDark by themeRepository.isDark.collectAsState()
    val session by authRepository.session.collectAsState()

    TodoListTheme(darkTheme = isDark) {
        Surface {
            when (session) {
                null -> AuthNavDisplay()
                else -> TodoNavDisplay()
            }
        }
    }
}

@Composable
private fun AuthNavDisplay() {
    val backStack = rememberNavBackStack(Routes.AuthRoute)

    CompositionLocalProvider(LocalBackStack provides backStack) {
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Routes.AuthRoute> { AuthScreen() }
            }
        )
    }
}

@Composable
private fun TodoNavDisplay() {
    val backStack = rememberNavBackStack(Routes.ListTodoRoute)

    CompositionLocalProvider(LocalBackStack provides backStack) {
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry<Routes.ListTodoRoute> { ListTodoScreen() }
                entry<Routes.TodoFormRoute> { CreateTodoScreen(todoId = it.todoId) }
                entry<Routes.DetailTodoRoute> { DetailTodoScreen(id = it.id) }
                entry<Routes.CategoryRoute> { CategoryScreen() }
            }
        )
    }
}