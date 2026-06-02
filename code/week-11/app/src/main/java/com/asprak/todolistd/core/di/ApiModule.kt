package com.asprak.todolistd.core.di

import com.asprak.todolistd.core.network.RetrofitProvider
import com.asprak.todolistd.feature.auth.data.AuthApi
import com.asprak.todolistd.feature.category.data.CategoryApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideAuthApi() = RetrofitProvider.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideCategoryApi() = RetrofitProvider.create(CategoryApi::class.java)
}