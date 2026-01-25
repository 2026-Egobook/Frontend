package com.egobook.app.di

import com.egobook.app.data.api.CounselingApiService
import com.egobook.app.data.api.NotificationApiService
import com.egobook.app.data.api.FriendsApiService
import com.egobook.app.data.api.QuestionApiService
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
    fun provideFriendsService(retrofit: Retrofit): FriendsApiService {
        return retrofit.create(FriendsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionService(retrofit: Retrofit): QuestionApiService {
        return retrofit.create(QuestionApiService::class.java)
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
