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
import com.asprak.todolistd.feature.auth.presentation.AuthScreen
import com.asprak.todolistd.feature.setting.data.ThemeRepository
import com.asprak.todolistd.feature.test.presentation.increment.IncrementTestScreen
import com.asprak.todolistd.feature.test.presentation.test.TestScreen
import com.asprak.todolistd.feature.todo.presentation.CreateTodoScreen
import com.asprak.todolistd.feature.todo.presentation.DetailTodoScreen
import com.asprak.todolistd.feature.todo.presentation.ListTodoScreen
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun ComposeApp(themeRepository: ThemeRepository) {
    val backStack = rememberNavBackStack(Routes.TestRoute)

    val isDark by themeRepository.isDark.collectAsState()

    CompositionLocalProvider(LocalBackStack provides backStack) {
        TodoListTheme(darkTheme = isDark) {
            Surface {
                NavDisplay(
                    backStack = backStack,
                    entryDecorators = listOf(
                        // Add the default decorators for managing scenes and saving state
                        rememberSaveableStateHolderNavEntryDecorator(),
                        // Then add the view model store decorator
                        rememberViewModelStoreNavEntryDecorator()
                    ),
                    entryProvider = entryProvider {
                        // test
                        entry<Routes.TestRoute> { TestScreen() }
                        entry<Routes.IncrementTestRoute> { IncrementTestScreen() }

                        // auth
                        entry<Routes.AuthRoute> { AuthScreen() }

                        // todo
                        entry<Routes.ListTodoRoute> { ListTodoScreen() }
                        entry<Routes.CreateTodoRoute> { CreateTodoScreen() }
                        entry<Routes.DetailTodoRoute> {
                            val id = it.id

                            DetailTodoScreen(id = id)
                        }
                    }
                )
            }
        }
    }
}