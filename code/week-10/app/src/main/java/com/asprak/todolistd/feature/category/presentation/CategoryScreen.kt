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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.asprak.todolistd.core.LocalBackStack
import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.ui.theme.TodoListTheme

@Composable
fun CategoryScreen(viewModel: CategoryViewModel = hiltViewModel()) {
    val backStack = LocalBackStack.current

    val uiState by viewModel.uiState.collectAsState()

    val selectedCategory = remember(uiState.categories, uiState.selectedCategoryId) {
        uiState.selectedCategoryId?.let { id -> uiState.categories.firstOrNull { category -> category.id == id } }
    }

    Content(
        categories = uiState.categories,
        selectedCategory = selectedCategory,
        name = uiState.name,
        onChangeName = viewModel::setName,
        onClickBack = { backStack.removeLastOrNull() },
        onClickSave = {
            viewModel.saveCategory()
        },
        onClickEdit = { category ->
            viewModel.selectCategory(category)
        },
        onClickDelete = { category ->
            viewModel.deleteCategory(category)
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


