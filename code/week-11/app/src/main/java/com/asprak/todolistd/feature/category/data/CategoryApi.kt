package com.asprak.todolistd.feature.category.data

import com.asprak.todolistd.feature.category.data.dto.CreateCategoryReq
import com.asprak.todolistd.feature.category.data.dto.CreateCategoryRes
import com.asprak.todolistd.feature.category.data.dto.DeleteCategoryRes
import com.asprak.todolistd.feature.category.data.dto.GetAllCategoriesRes
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface CategoryApi {
    @POST("api/categories")
    suspend fun create(
        @Header("Authorization") authorization: String,
        @Body body: CreateCategoryReq
    ): Response<CreateCategoryRes>

    @GET("api/categories")
    suspend fun getAll(
        @Header("Authorization") authorization: String
    ): Response<GetAllCategoriesRes>

    @DELETE("api/categories/{id}")
    suspend fun delete(
        @Header("Authorization") authorization: String,
        @Path("id") categoryId: Int
    ): Response<DeleteCategoryRes>
}