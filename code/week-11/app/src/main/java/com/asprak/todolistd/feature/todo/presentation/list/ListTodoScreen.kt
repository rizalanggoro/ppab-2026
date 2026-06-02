package com.asprak.todolistd.feature.todo.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.core.Routes
import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.domain.Todo
import com.asprak.todolistd.feature.todo.component.TodoListItem

private enum class TodoFilter {
    ALL,
    TODO,
    DONE,
}

@Composable
fun ListTodoScreen(viewModel: ListTodoViewModel = hiltViewModel()) {
    val backStack = LocalBackStack.current

//    val uiState by viewModel.uiState.collectAsState()

    val filter = rememberSaveable { mutableStateOf(TodoFilter.ALL) }

//    val filteredTodos = when (filter.value) {
//        TodoFilter.ALL -> uiState.todos
//        TodoFilter.TODO -> uiState.todos.filterNot { it.isDone }
//        TodoFilter.DONE -> uiState.todos.filter { it.isDone }
//    }

    Content(
//        todos = filteredTodos,
//        categories = uiState.categories,
//        onClickCreate = {
//            backStack.add(Routes.TodoFormRoute())
//        },
        onClickManageCategory = {
            backStack.add(Routes.CategoryRoute)
        },
//        onClickLogout = {
//            viewModel.logout()
//        },
//        filter = filter.value,
//        onChangeFilter = {
//            filter.value = it
//        },
//        onClickTodo = {
//            backStack.add(Routes.DetailTodoRoute(it))
//        },
//        onToggleTodo = { todoId ->
//            viewModel.toggleTodoDone(todoId)
//        }
    )
}

@Composable
private fun Content(
    todos: List<Todo> = emptyList(),
    categories: List<Category> = emptyList(),
    isLoading: Boolean = false,
    onClickCreate: () -> Unit = {},
    onClickManageCategory: () -> Unit = {},
    onClickLogout: () -> Unit = {},
    filter: TodoFilter = TodoFilter.ALL,
    onChangeFilter: (TodoFilter) -> Unit = {},
    onClickTodo: (Int) -> Unit = {},
    onToggleTodo: (Int) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Todo List")
                },
                actions = {
                    TextButton(onClick = onClickManageCategory) { Text("Kategori") }
                    TextButton(onClick = onClickLogout) { Text("Keluar") }
                }
            )
        },
        floatingActionButton = {
            when (isLoading) {
                true -> Unit
                else -> FloatingActionButton(onClick = onClickCreate) { Text("+") }
            }
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filter == TodoFilter.ALL,
                    onClick = { onChangeFilter(TodoFilter.ALL) },
                    label = { Text("Semua") }
                )
                FilterChip(
                    selected = filter == TodoFilter.TODO,
                    onClick = { onChangeFilter(TodoFilter.TODO) },
                    label = { Text("Belum selesai") }
                )
                FilterChip(
                    selected = filter == TodoFilter.DONE,
                    onClick = { onChangeFilter(TodoFilter.DONE) },
                    label = { Text("Selesai") }
                )
            }

            if (todos.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "Belum ada todo",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            } else {
                LazyColumn {
                    items(todos) { todo ->
                        val categoryName =
                            categories.firstOrNull { category -> category.id == todo.categoryId }?.name
                                ?: "Tanpa kategori"
                        TodoListItem(
                            todo = todo,
                            categoryName = categoryName,
                            onClickTodo = { onClickTodo(todo.id) },
                            onToggleDone = { onToggleTodo(todo.id) }
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
    Content()
}

@Composable
@Preview
private fun TodoLoadingPreview() {
    Content(isLoading = true)
}

