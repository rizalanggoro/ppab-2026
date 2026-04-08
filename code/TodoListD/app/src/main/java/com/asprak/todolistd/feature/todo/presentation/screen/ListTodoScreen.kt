package com.asprak.todolistd.feature.todo.presentation.screen

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.asprak.todolistd.feature.todo.presentation.activity.CreateTodoActivity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListTodoScreen(
    isLoading: Boolean = false
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Todo List")
                }
            )
        },
        floatingActionButton = {
            when (isLoading) {
                true -> Box {}
                else -> FloatingActionButton(
                    onClick = {
                        context.startActivity(
                            Intent(
                                context,
                                CreateTodoActivity::class.java
                            )
                        )
                    }
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
                Text("Loading...", modifier = Modifier.align(Alignment.Center))
            }

            false -> LazyColumn(modifier = Modifier.padding(it)) {
                items(20) {
                    Text(
                        "item ke-$it",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Composable
@Preview
private fun ListTodoPreview() {
    ListTodoScreen()
}

@Composable
@Preview
private fun ListTodoLoadingPreview() {
    ListTodoScreen(isLoading = true)
}

