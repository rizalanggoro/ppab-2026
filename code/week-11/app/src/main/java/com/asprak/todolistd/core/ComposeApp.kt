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
import com.asprak.todolistd.core.prefs.TokenPrefs
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
    tokenPrefs: TokenPrefs
) {
    val isDark by themeRepository.isDark.collectAsState()
    val token by tokenPrefs.token.collectAsState()

    val startRoute = when {
        token != null -> Routes.ListTodoRoute
        else -> Routes.AuthRoute
    }

    val backStack = rememberNavBackStack(startRoute)

    TodoListTheme(darkTheme = isDark) {
        Surface {
            CompositionLocalProvider(LocalBackStack provides backStack) {
                NavDisplay(
                    backStack = backStack,
                    entryDecorators = listOf(
                        rememberSaveableStateHolderNavEntryDecorator(),
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        // auth
                        entry<Routes.AuthRoute> { AuthScreen() }

                        // todo
                        entry<Routes.ListTodoRoute> { ListTodoScreen() }
                        entry<Routes.TodoFormRoute> { CreateTodoScreen(todoId = it.todoId) }
                        entry<Routes.DetailTodoRoute> { DetailTodoScreen(id = it.id) }

                        // category
                        entry<Routes.CategoryRoute> { CategoryScreen() }
                    }
                )
            }
        }
    }
}