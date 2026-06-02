package com.asprak.todolistd.feature.category.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class CreateCategoryReq(
    val name: String,
)
