@file:Suppress("UNUSED_VALUE")

package com.asprak.todolistd.feature.todo.presentation.detail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.domain.Todo
import com.asprak.todolistd.ui.theme.TodoListTheme

@Suppress("UNUSED_VALUE")
@Composable
fun DetailTodoScreen(
    id: Int = 0,
    viewModel: DetailTodoViewModel = hiltViewModel()
) {
    val backStack = LocalBackStack.current
//    val uiState by viewModel.uiState.collectAsState()

    var isDeleteDialogOpen by remember { mutableStateOf(false) }

//    LaunchedEffect(id) {
//        viewModel.loadTodo(id)
//    }
//
//    // Listen to delete events
//    LaunchedEffect(Unit) {
//        viewModel.deleteEvent.collect {
//            backStack.removeLastOrNull()
//        }
//    }

    Content(
//        todo = uiState.todo,
//        categoryName = uiState.categoryName ?: "Tanpa kategori",
//        onClickBack = {
//            backStack.removeLastOrNull()
//        },
//        onClickEdit = {
//            backStack.add(Routes.TodoFormRoute(todoId = id))
//        },
//        onClickDelete = {
//            isDeleteDialogOpen = true
//        },
//        onToggleDone = {
//            viewModel.toggleTodoDone()
//        }
    )

//    if (isDeleteDialogOpen) {
//        DeleteDialog(
//            onDismissRequest = {
//                isDeleteDialogOpen = false
//            },
//            onConfirm = {
//                isDeleteDialogOpen = false
//                viewModel.deleteTodo()
//            }
//        )
//    }
}

@Composable
private fun Content(
    todo: Todo? = null,
    categoryName: String = "Tanpa kategori",
    onClickBack: () -> Unit = {},
    onClickEdit: () -> Unit = {},
    onClickDelete: () -> Unit = {},
    onToggleDone: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = null
                        )
                    }
                },
                title = { Text("Detail Todo") },
                actions = {
                    IconButton(onClick = onClickEdit) {
                        Icon(Icons.Rounded.Edit, contentDescription = null)
                    }
                    IconButton(onClick = onClickDelete) {
                        Icon(Icons.Rounded.Delete, contentDescription = null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            todo?.let {
                Checkbox(
                    checked = it.isDone,
                    onCheckedChange = { onToggleDone() }
                )
                Text(it.title, style = MaterialTheme.typography.titleMedium)
                Text(it.description, style = MaterialTheme.typography.bodyMedium)
                Text(categoryName, style = MaterialTheme.typography.labelMedium)
                Text("id: ${it.id}", style = MaterialTheme.typography.labelMedium)
            } ?: run {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text("Todo tidak ditemukan", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}

@Composable
private fun DeleteDialog(
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("Hapus Todo")
        },
        text = {
            Text("Apakah Anda yakin ingin menghapus todo ini?")
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Batal")
            }
        }
    )
}

@Preview(group = "Screen")
@Composable
private fun DetailTodoPreview() {
    TodoListTheme {
        Content()
    }
}


@Preview(group = "Dialog")
@Composable
private fun DeleteDialogPreview() {
    TodoListTheme {
        DeleteDialog()
    }
}