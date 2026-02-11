package com.egobook.app.di.module

import com.egobook.app.data.api.AIApiService
import com.egobook.app.data.api.AccountApiService
import com.egobook.app.data.api.AuthApiService
import com.egobook.app.data.api.CounselingApiService
import com.egobook.app.data.api.DiaryApiService
import com.egobook.app.data.api.FriendsApiService
import com.egobook.app.data.api.LetterApiService
import com.egobook.app.data.api.NotificationApiService
import com.egobook.app.data.api.QuestionApiService
import com.egobook.app.di.qualifier.AIApi
import com.egobook.app.di.qualifier.AuthRetrofit
import com.egobook.app.di.qualifier.BackendApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideCounselingService(@BackendApi retrofit: Retrofit): CounselingApiService {
        return retrofit.create(CounselingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationService(@BackendApi retrofit: Retrofit): NotificationApiService {
        return retrofit.create(NotificationApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideFriendsService(@BackendApi retrofit: Retrofit): FriendsApiService {
        return retrofit.create(FriendsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideQuestionService(@BackendApi retrofit: Retrofit): QuestionApiService {
        return retrofit.create(QuestionApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideLetterService(@BackendApi retrofit: Retrofit): LetterApiService {
        return retrofit.create(LetterApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAIService(@AIApi retrofit: Retrofit): AIApiService {
        return retrofit.create(AIApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthService(@AuthRetrofit retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideAccountService(@BackendApi retrofit: Retrofit): AccountApiService =
        retrofit.create(AccountApiService::class.java)


    @Provides
    @Singleton
    fun provideDiaryService(@BackendApi retrofit: Retrofit): DiaryApiService =
        retrofit.create(DiaryApiService::class.java)
}