package com.asprak.todolistd.feature.todo.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.core.Routes
import com.asprak.todolistd.core.dummyTodos
import com.asprak.todolistd.domain.Todo
import com.asprak.todolistd.feature.todo.component.TodoListItem

@Composable
fun ListTodoScreen() {
    val backStack = LocalBackStack.current

    Content(
        todos = dummyTodos,
        onClickCreate = {
            backStack.add(Routes.CreateTodoRoute)
        },
        onClickTodo = {
            backStack.add(Routes.DetailTodoRoute(it))
        }
    )
}

@Composable
private fun Content(
    todos: List<Todo> = emptyList(),
    isLoading: Boolean = false,
    onClickCreate: () -> Unit = {},
    onClickTodo: (Int) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Todo")
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            Icons.Rounded.Refresh,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            when (isLoading) {
                true -> Unit
                else -> FloatingActionButton(
                    onClick = onClickCreate
                ) {
                    Icon(
                        Icons.Rounded.Add,
                        contentDescription = null,
                    )
                }
            }
        }
    ) {
        when (isLoading) {
            true -> Box(modifier = Modifier.fillMaxSize()) {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            false -> Column(modifier = Modifier.padding(it)) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Semua") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Belum selesai") }
                    )
                    FilterChip(
                        selected = false,
                        onClick = {},
                        label = { Text("Selesai") }
                    )
                }

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(todos) {
                        TodoListItem(
                            todo = it,
                            onClickTodo = {
                                onClickTodo(it.id)
                            }
                        )
                    }

                    item {
                        Box(modifier = Modifier.height((56 + 32).dp))
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun TodoPreview() {
    Content(todos = dummyTodos)
}

@Composable
@Preview
private fun TodoLoadingPreview() {
    Content(isLoading = true)
}

