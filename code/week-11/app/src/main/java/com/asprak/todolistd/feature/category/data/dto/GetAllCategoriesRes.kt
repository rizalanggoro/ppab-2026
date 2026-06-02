package com.asprak.todolistd.feature.category.data.dto

import com.asprak.todolistd.dto.DtoCategory
import kotlinx.serialization.Serializable

@Serializable
data class GetAllCategoriesRes(
    val items: List<DtoCategory>
)
