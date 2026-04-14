package com.asprak.todolistd.core

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
object Routes {
    @Serializable
    data object AuthRoute : NavKey

    @Serializable
    data object ListTodoRoute : NavKey

    @Serializable
    data object CreateTodoRoute : NavKey

    @Serializable
    data class DetailTodoRoute(
        val id: String
    ) : NavKey
}