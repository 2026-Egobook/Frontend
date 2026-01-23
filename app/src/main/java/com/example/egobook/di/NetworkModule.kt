package com.example.egobook.di

import com.example.egobook.data.api.CounselingApiService
import com.example.egobook.data.api.NotificationApiService
import com.example.egobook.data.api.SquareApiService
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
    fun provideCounselingService(retrofit: Retrofit): CounselingApiService {
        return retrofit.create(CounselingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationService(retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSquareService(retrofit: Retrofit): SquareApiService {
        return retrofit.create(SquareApiService::class.java)
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
