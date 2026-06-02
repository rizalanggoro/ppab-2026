package com.asprak.todolistd.feature.category.data

import com.asprak.todolistd.core.extension.bearerToken
import com.asprak.todolistd.core.extension.errorMessage
import com.asprak.todolistd.core.prefs.TokenPrefs
import com.asprak.todolistd.dto.DtoCategory
import com.asprak.todolistd.feature.category.data.dto.CreateCategoryReq
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val tokenPrefs: TokenPrefs,
    private val categoryApi: CategoryApi
) {
    suspend fun create(name: String): DtoCategory {
        val response = categoryApi.create(
            authorization = tokenPrefs.bearerToken(),
            body = CreateCategoryReq(
                name = name
            )
        )

        if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

        val body = response.body() ?: throw IllegalStateException("Response body is null")
        return body.item
    }

    suspend fun getAll(): List<DtoCategory> {
        val response = categoryApi.getAll(
            authorization = tokenPrefs.bearerToken(),
        )

        if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

        val body = response.body() ?: throw IllegalStateException("Response body is null")
        return body.items
    }

    suspend fun delete(categoryId: Int): String {
        val response = categoryApi.delete(
            authorization = tokenPrefs.bearerToken(),
            categoryId = categoryId
        )

        if (!response.isSuccessful) throw IllegalStateException(response.errorMessage())

        val body = response.body() ?: throw IllegalStateException("Response body is null")
        return body.message
    }
}


