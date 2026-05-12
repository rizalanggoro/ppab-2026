package com.asprak.todolistd.feature.todo.presentation.create

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.core.MyApplication
import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.ui.theme.TodoListTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTodoScreen(
    todoId: Int? = null,
    vm: CreateTodoViewModel = viewModel(factory = CreateTodoViewModel.Factory)
) {
    val context = LocalContext.current.applicationContext as MyApplication
    val backStack = LocalBackStack.current

    val authRepository = context.authRepository
    val todoRepository = context.todoRepository
    val categoryRepository = context.categoryRepository

    val session by authRepository.session.collectAsState()
    val ownerEmail = session?.email ?: return

    val categories by categoryRepository.observeCategories(ownerEmail)
        .collectAsState(initial = emptyList())
    val todos by todoRepository.observeTodos(ownerEmail).collectAsState(initial = emptyList())
    val uiState by vm.uiState.collectAsState()

    // Update ViewModel with categories
    LaunchedEffect(categories) {
        vm.setCategories(categories)
    }

    // Set existing todo ID and load data
    LaunchedEffect(todoId, todos) {
        vm.setExistingTodoId(todoId)
        todoId?.let { id ->
            todos.firstOrNull { it.id == id }?.let { existingTodo ->
                vm.setTitle(existingTodo.title)
                vm.setDescription(existingTodo.description)
                vm.setCategoryId(existingTodo.categoryId)
            }
        }
    }

    var isBottomSheetOpen by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Listen to navigation events
    LaunchedEffect(Unit) {
        vm.navigationEvent.collect {
            backStack.removeLastOrNull()
        }
    }

    val selectedCategory = categories.firstOrNull { category -> category.id == uiState.categoryId }

    Content(
        isEditing = todoId != null,
        title = uiState.title,
        detail = uiState.description,
        selectedCategory = selectedCategory,
        onChangeTitle = { vm.setTitle(it) },
        onChangeDetail = { vm.setDescription(it) },
        onClickSelectCategory = { isBottomSheetOpen = true },
        onClickBack = { backStack.removeLastOrNull() },
        onClickSave = {
            vm.saveTodo(ownerEmail)
        }
    )

    if (isBottomSheetOpen) {
        BottomSheetSelectCategory(
            sheetState = bottomSheetState,
            selectedCategoryId = uiState.categoryId,
            categories = categories,
            onClickItem = {
                vm.setCategoryId(it.id)
                isBottomSheetOpen = false
            },
            onDismissRequest = {
                isBottomSheetOpen = false
            }
        )
    }
}

@Composable
private fun Content(
    isEditing: Boolean = false,
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
                    Text(if (isEditing) "Edit Todo" else "Create Todo")
                }
            )
        }
    ) {
        Column(modifier = Modifier.padding(it)) {
            Column(
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
                        modifier = Modifier.fillMaxWidth()
                    )
                    TextField(
                        value = detail,
                        onValueChange = onChangeDetail,
                        label = { Text("Detail") },
                        modifier = Modifier.fillMaxWidth(),
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
                        Text(selectedCategory?.name ?: "Pilih kategori")
                    },
                    trailingContent = {
                        Icon(
                            Icons.Rounded.ChevronRight,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable {
                        onClickSelectCategory()
                    }
                )
            }

            Button(
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
    val sampleCategories = listOf(
        Category(id = 1, name = "Praktikum", ownerEmail = "demo@example.com")
    )

    TodoListTheme {
        Content(
            selectedCategory = sampleCategories.firstOrNull()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(group = "Bottom Sheet")
@Composable
private fun BottomSheetSelectCategoryPreview() {
    val sampleCategories = listOf(
        Category(id = 1, name = "Praktikum", ownerEmail = "demo@example.com")
    )

    TodoListTheme {
        BottomSheetSelectCategory(
            categories = sampleCategories
        )
    }
}

