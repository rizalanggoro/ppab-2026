package com.asprak.todolistd.feature.todo.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ListTodoScreen() {
    Content()
}

@Composable
private fun Content(
    isLoading: Boolean = false
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
                true -> Box {}
                else -> FloatingActionButton(
                    onClick = {
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
                LoadingIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            false -> Column(modifier = Modifier.padding(it)) {
                FilterChip(
                    selected = false,
                    onClick = {},
                    label = { Text("Semua") }
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(3) {
                        ListItem(
                            leadingContent = {
                                Checkbox(checked = false, onCheckedChange = null)
                            },
                            headlineContent = {
                                Text("Mengerjakan tugas akhir")
                            },
                            supportingContent = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("Skripsi")
                                    Icon(
                                        Icons.Rounded.Circle,
                                        contentDescription = null,
                                        modifier = Modifier.size(4.dp)
                                    )
                                    Text("12 Juni 2022")
                                }
                            }
                        )
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

