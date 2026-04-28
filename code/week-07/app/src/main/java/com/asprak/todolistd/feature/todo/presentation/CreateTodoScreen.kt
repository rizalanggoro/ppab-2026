package com.asprak.todolistd.feature.todo.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Category
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material3.Button
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.core.dummyCategories
import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.ui.theme.TodoListTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTodoScreen() {
    val backStack = LocalBackStack.current

    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Content(
        selectedCategory = dummyCategories.firstOrNull {
            it.id == selectedCategoryId
        },
        onClickSelectCategory = {
            isBottomSheetOpen = true
        },
        onClickBack = {
            backStack.removeLastOrNull()
        },
        onClickSave = {}
    )

    if (isBottomSheetOpen) {
        BottomSheetSelectCategory(
            sheetState = bottomSheetState,
            selectedCategoryId = selectedCategoryId,
            categories = dummyCategories,
            onClickItem = {
                scope.launch {
                    selectedCategoryId = it.id
                    if (bottomSheetState.isVisible) {
                        bottomSheetState.hide()
                    }
                }.invokeOnCompletion { isBottomSheetOpen = false }
            },
            onDismissRequest = {
                scope.launch {
                    if (bottomSheetState.isVisible) {
                        bottomSheetState.hide()
                    }
                }.invokeOnCompletion { isBottomSheetOpen = false }
            }
        )
    }
}

@Composable
private fun Content(
    isLoading: Boolean = false,
    title: String = "",
    onChangeTitle: (String) -> Unit = {},
    detail: String = "",
    onChangeDetail: (String) -> Unit = {},
    selectedCategory: Category? = null,
    onClickSelectCategory: () -> Unit = {},
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
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp),
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

                ListItem(
                    leadingContent = {
                        Icon(
                            Icons.Rounded.Category,
                            contentDescription = null
                        )
                    },
                    headlineContent = {
                        Text("Kategori")
                    },
                    supportingContent = {
                        Text(
                            selectedCategory?.name.let { category ->
                                if (category.isNullOrEmpty()) "Pilih kategori"
                                else category
                            }
                        )
                    },
                    trailingContent = {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable(enabled = !isLoading) {
                        onClickSelectCategory()
                    }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BottomSheetSelectCategory(
    sheetState: SheetState = rememberModalBottomSheetState(),
    selectedCategoryId: Int? = null,
    categories: List<Category> = emptyList(),
    onClickItem: (Category) -> Unit = {},
    onDismissRequest: () -> Unit = {},
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState
    ) {
        LazyColumn {
            items(categories) {
                ListItem(
                    leadingContent = {
                        RadioButton(
                            selected = it.id == selectedCategoryId,
                            onClick = null
                        )
                    },
                    headlineContent = {
                        Text(it.name)
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = Color.Transparent
                    ),
                    modifier = Modifier.clickable {
                        onClickItem(it)
                    }
                )
            }
        }

        OutlinedButton(
            onClick = onDismissRequest,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text("Batal")
        }
    }
}

@Preview(group = "Screen")
@Composable
private fun CreateTodoPreview() {
    TodoListTheme {
        Content()
    }
}

@Preview(group = "Screen")
@Composable
private fun CreateTodoWithCategoryPreview() {
    TodoListTheme {
        Content(
            selectedCategory = dummyCategories.firstOrNull()
        )
    }
}

@Preview(group = "Screen")
@Composable
private fun CreateTodoLoadingPreview() {
    TodoListTheme {
        Content(isLoading = true)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(group = "Bottom Sheet")
@Composable
private fun BottomSheetSelectCategoryPreview() {
    TodoListTheme {
        BottomSheetSelectCategory(
            categories = dummyCategories
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(group = "Bottom Sheet")
@Composable
private fun BottomSheetSelectCategorySelectedPreview() {
    TodoListTheme {
        BottomSheetSelectCategory(
            categories = dummyCategories,
            selectedCategoryId = 1
        )
    }
}