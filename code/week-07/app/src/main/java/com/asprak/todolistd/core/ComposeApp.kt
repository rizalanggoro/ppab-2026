package com.asprak.todolistd.core

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.asprak.todolistd.feature.auth.presentation.AuthScreen
import com.asprak.todolistd.feature.todo.presentation.CreateTodoScreen
import com.asprak.todolistd.feature.todo.presentation.DetailTodoScreen
import com.asprak.todolistd.feature.todo.presentation.ListTodoScreen
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun ComposeApp() {
    val backStack = rememberNavBackStack(Routes.AuthRoute)

    CompositionLocalProvider(LocalBackStack provides backStack) {
        TodoListTheme {
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