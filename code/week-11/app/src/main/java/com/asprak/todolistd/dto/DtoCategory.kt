package com.asprak.todolistd.dto

import com.asprak.todolistd.domain.Category
import kotlinx.serialization.Serializable

@Serializable
data class DtoCategory(
    val data: Category
)
