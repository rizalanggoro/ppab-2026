package com.asprak.todolistd.feature.todo.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Circle
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.domain.Todo

@Composable
fun TodoListItem(
    todo: Todo,
    onClickTodo: () -> Unit = {},
) {
    ListItem(
        leadingContent = {
            Checkbox(checked = todo.isDone, onCheckedChange = null)
        },
        headlineContent = {
            Text(
                todo.title,
                textDecoration = when (todo.isDone) {
                    true -> TextDecoration.LineThrough
                    false -> null
                }
            )
        },
        supportingContent = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    todo.category?.name ?: "No Category",
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = when (todo.isDone) {
                        true -> TextDecoration.LineThrough
                        false -> null
                    }
                )
                Icon(
                    Icons.Rounded.Circle,
                    contentDescription = null,
                    modifier = Modifier.size(4.dp)
                )
                Text(
                    "12 Juni 2022",
                    style = MaterialTheme.typography.bodySmall,
                    textDecoration = when (todo.isDone) {
                        true -> TextDecoration.LineThrough
                        false -> null
                    }
                )
            }
        },
        modifier = Modifier.clickable {
            onClickTodo()
        }
    )
}

private val dummyTodo = Todo(
    id = 1,
    title = "Mengerjakan tugas PPAB",
    description = "Lorem ipsum dolor sit amet, consectetur adipiscing " +
            "elit. Donec auctor, nisl eget ultricies lacinia, nunc nisl " +
            "aliquam nisl, eget aliquam nunc nisl eget nunc.",
    isDone = false,
    category = Category(
        id = 1,
        name = "Praktikum"
    )
)

@Preview
@Composable
private fun ListItemPreview() {
    TodoListItem(
        todo = dummyTodo
    )
}

@Preview
@Composable
private fun ListItemDonePreview() {
    TodoListItem(
        todo = dummyTodo.copy(
            isDone = true
        )
    )
}