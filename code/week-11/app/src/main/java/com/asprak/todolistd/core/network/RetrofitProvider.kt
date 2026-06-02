package com.asprak.todolistd.core.network

import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object RetrofitProvider {
    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(NetworkConfig.BASE_URL)
            .addConverterFactory(
                json.asConverterFactory(
                    "application/json".toMediaType()
                )
            )
            .build()
    }

    fun <T> create(service: Class<T>): T = retrofit.create(service)
}