package com.asprak.todolistd.core

import com.asprak.todolistd.domain.Category
import com.asprak.todolistd.domain.Todo
import kotlin.random.Random

val dummyCategories = List(3) {
    Category(
        id = it,
        name = "Category $it"
    )
}

val dummyTodos = List(10) {
    Todo(
        id = it,
        title = "Dummy todo ke-$it",
        description = "Lorem ipsum dolor sit amet consectetur adipiscing elit. " +
                "Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
        isDone = Random.nextBoolean(),
        category = dummyCategories.random()
    )
}