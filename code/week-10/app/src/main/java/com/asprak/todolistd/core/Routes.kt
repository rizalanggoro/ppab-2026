package com.asprak.todolistd.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object Routes {
    @Serializable
    data object TestRoute : NavKey

    @Serializable
    data object IncrementTestRoute : NavKey

    @Serializable
    data object AuthRoute : NavKey

    @Serializable
    data object ListTodoRoute : NavKey

    @Serializable
    data class TodoFormRoute(
        val todoId: Int? = null
    ) : NavKey

    @Serializable
    data object CategoryRoute : NavKey

    @Serializable
    data class DetailTodoRoute(
        val id: Int
    ) : NavKey
}