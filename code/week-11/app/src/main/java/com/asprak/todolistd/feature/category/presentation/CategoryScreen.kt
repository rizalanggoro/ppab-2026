package com.asprak.todolistd.feature.category.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.dto.DtoCategory
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun CategoryScreen(vm: CategoryViewModel = hiltViewModel()) {
    val backStack = LocalBackStack.current
    val uiState by vm.uiState.collectAsState()

    Content(
        uiState = uiState,
        onClickCreate = vm::create,
        onChangeName = vm::setName,
        onClickBack = { backStack.removeLastOrNull() },
        onClickDelete = vm::deleteCategory,
    )
}

@Composable
private fun Content(
    uiState: CategoryUiState = CategoryUiState(),
    onChangeName: (String) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickCreate: () -> Unit = {},
    onClickDelete: (Category) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            null
                        )
                    }
                },
                title = { Text("Kelola Kategori") }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = uiState.name,
                    onValueChange = onChangeName,
                    label = { Text("Nama kategori") },
                    modifier = Modifier.fillMaxWidth()
                )
                when {
                    uiState.isCreating -> Box(
                        modifier = Modifier
                            .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        LoadingIndicator()
                    }

                    else -> Button(
                        onClick = onClickCreate,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tambah")
                    }
                }
            }

            when {
                uiState.isLoading -> Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    LoadingIndicator()
                }

                else -> LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    items(uiState.categories) { category ->
                        ListItem(
                            headlineContent = {
                                Text(
                                    category.data.name
                                )
                            },
                            trailingContent = {
                                when {
                                    category.data.id == uiState.isDeletingId -> LoadingIndicator()
                                    
                                    else -> IconButton(
                                        onClick = {
                                            onClickDelete(
                                                category.data
                                            )
                                        }
                                    ) {
                                        Icon(
                                            Icons.Rounded.Delete,
                                            null
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    TodoListTheme {
        Content(
            uiState = CategoryUiState(
                categories = listOf(
                    DtoCategory(
                        data = Category(
                            id = 1,
                            name = "Pekerjaan",
                            userId = 1
                        )
                    )
                )
            )
        )
    }
}

@Preview
@Composable
private fun LoadingPreview() {
    TodoListTheme {
        Content(
            uiState = CategoryUiState(
                isLoading = true
            )
        )
    }
}

@Preview
@Composable
private fun CreatingPreview() {
    TodoListTheme {
        Content(
            uiState = CategoryUiState(
                isCreating = true
            )
        )
    }
}


