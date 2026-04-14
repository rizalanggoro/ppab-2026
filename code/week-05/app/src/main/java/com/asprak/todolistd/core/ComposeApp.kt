package com.asprak.todolistd.core

import androidx.compose.runtime.Composable
import androidx.navigation3.ui.NavDisplay
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun MyApp() {
    TodoListTheme {
        NavDisplay(
            sceneState = TODO(),
            navigationEventState = TODO()
        )
    }
}