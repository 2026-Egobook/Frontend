package com.example.egobook_frontent.di

import com.example.egobook_frontent.data.api.CounselingApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    @Provides
    @Singleton
    fun provideCounselingApi(retrofit: Retrofit): CounselingApiService {
        return retrofit.create(CounselingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.egobook.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}