package com.asprak.todolistd.feature.category.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.core.MyApplication
import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun CategoryScreen() {
    val context = LocalContext.current.applicationContext as MyApplication
    val backStack = LocalBackStack.current

    val session by context.authRepository.session.collectAsState()
    val ownerEmail = session?.email ?: return

    val categories by context.categoryRepository.observeCategories(ownerEmail)
        .collectAsState(initial = emptyList())

    var selectedCategoryId by rememberSaveable { mutableStateOf<Int?>(null) }
    var name by rememberSaveable { mutableStateOf("") }

    val selectedCategory = remember(categories, selectedCategoryId) {
        selectedCategoryId?.let { id -> categories.firstOrNull { category -> category.id == id } }
    }

    Content(
        categories = categories,
        selectedCategory = selectedCategory,
        name = name,
        onChangeName = { name = it },
        onClickBack = { backStack.removeLastOrNull() },
        onClickSave = {
            if (name.isNotBlank()) {
                when {
                    selectedCategory == null -> context.categoryRepository.create(ownerEmail, name)
                    else -> context.categoryRepository.update(ownerEmail, selectedCategory.id, name)
                }

                selectedCategoryId = null
                name = ""
            }
        },
        onClickEdit = { category ->
            selectedCategoryId = category.id
            name = category.name
        },
        onClickDelete = { category ->
            context.categoryRepository.delete(ownerEmail, category.id)
            if (selectedCategoryId == category.id) {
                selectedCategoryId = null
                name = ""
            }
        },
    )
}

@Composable
private fun Content(
    categories: List<Category> = emptyList(),
    selectedCategory: Category? = null,
    name: String = "",
    onChangeName: (String) -> Unit = {},
    onClickBack: () -> Unit = {},
    onClickSave: () -> Unit = {},
    onClickEdit: (Category) -> Unit = {},
    onClickDelete: (Category) -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onClickBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                },
                title = { Text("Category") }
            )
        }
    ) {
        Column(
            modifier = Modifier.padding(it),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextField(
                    value = name,
                    onValueChange = onChangeName,
                    label = { Text(if (selectedCategory == null) "Tambah category" else "Edit category") },
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = onClickSave,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(if (selectedCategory == null) "Save" else "Update")
                }
            }

            LazyColumn {
                items(categories) { category ->
                    ListItem(
                        headlineContent = { Text(category.name) },
                        supportingContent = { Text(category.ownerEmail) },
                        trailingContent = {
                            Row {
                                IconButton(onClick = { onClickEdit(category) }) {
                                    Icon(Icons.Rounded.Edit, contentDescription = null)
                                }
                                IconButton(onClick = { onClickDelete(category) }) {
                                    Icon(Icons.Rounded.Delete, contentDescription = null)
                                }
                            }
                        },
                        modifier = Modifier.clickable { onClickEdit(category) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun CategoryPreview() {
    val sampleCategories = listOf(
        Category(id = 1, name = "Category 1", ownerEmail = "demo@example.com"),
        Category(id = 2, name = "Category 2", ownerEmail = "demo@example.com")
    )

    TodoListTheme {
        Content(categories = sampleCategories)
    }
}


