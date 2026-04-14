package com.asprak.todolistd.feature.todo.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun DetailTodoScreen() {

}

@Composable
private fun Content(
    isLoading: Boolean = false,
    onClickBack: () -> Unit = {}
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
                        IconButton(onClick = {}) {
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
            }
        }
    }
}

@Preview
@Composable
private fun DetailTodoPreview() {
    TodoListTheme {
        Content()
    }
}

@Preview
@Composable
private fun DetailTodoLoadingPreview() {
    TodoListTheme {
        Content(isLoading = true)
    }
}