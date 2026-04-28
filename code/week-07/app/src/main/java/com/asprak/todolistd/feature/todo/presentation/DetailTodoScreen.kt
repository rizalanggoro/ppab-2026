package com.asprak.todolistd.feature.todo.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun DetailTodoScreen(
    id: Int = 0
) {
    val context = LocalContext.current
    val backStack = LocalBackStack.current

    var isDeleteDialogOpen by remember { mutableStateOf(false) }

    Content(
        id = id,
        onClickBack = {
            backStack.removeLastOrNull()
        },
        onClickDelete = {
            isDeleteDialogOpen = true
        }
    )

    if (isDeleteDialogOpen) {
        DeleteDialog(
            onDismissRequest = {
                isDeleteDialogOpen = false
            },
            onConfirm = {
                isDeleteDialogOpen = false

                // hapus todo

                backStack.removeLastOrNull()
                Toast.makeText(
                    context, "Todo berhasil dihapus!",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}

@Composable
private fun Content(
    id: Int = 0,
    isLoading: Boolean = false,
    onClickBack: () -> Unit = {},
    onClickDelete: () -> Unit = {}
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
                    if (!isLoading) {
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.Edit, contentDescription = null)
                        }
                        IconButton(onClick = onClickDelete) {
                            Icon(Icons.Rounded.Delete, contentDescription = null)
                        }
                    }
                }
            )
        }
    ) {
        when (isLoading) {
            true -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
            ) {
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            else -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it)
                    .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text("Todo title", style = MaterialTheme.typography.titleMedium)
                Text("Todo detail", style = MaterialTheme.typography.bodyMedium)
                Text("id: $id", style = MaterialTheme.typography.labelMedium)
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

@Preview(group = "Screen")
@Composable
private fun DetailTodoLoadingPreview() {
    TodoListTheme {
        Content(isLoading = true)
    }
}

@Preview(group = "Dialog")
@Composable
private fun DeleteDialogPreview() {
    TodoListTheme {
        DeleteDialog()
    }
}