package com.asprak.todolistd.feature.todo.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun CreateTodoScreen() {
    Content()
}

@Composable
private fun Content(
    isLoading: Boolean = false,
    title: String = "",
    onChangeTitle: (String) -> Unit = {},
    detail: String = "",
    onChangeDetail: (String) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickSave: () -> Unit = {}
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
                title = {
                    Text("Create Todo")
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = title,
                    onValueChange = onChangeTitle,
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading
                )
                TextField(
                    value = detail,
                    onValueChange = onChangeDetail,
                    label = { Text("Detail") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isLoading,
                    minLines = 5
                )
            }

            when (isLoading) {
                true -> ContainedLoadingIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(16.dp)
                )

                else -> Button(
                    onClick = onClickSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@Preview
@Composable
private fun CreateTodoPreview() {
    TodoListTheme {
        Content()
    }
}

@Preview
@Composable
private fun CreateTodoLoadingPreview() {
    TodoListTheme {
        Content(isLoading = true)
    }
}