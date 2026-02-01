package com.egobook.app.di.module

import com.egobook.app.data.repository.CounselingRepositoryImpl
import com.egobook.app.data.repository.DiaryRepositoryImpl
import com.egobook.app.data.repository.FriendsRepositoryImpl
import com.egobook.app.data.repository.LetterRepositoryImpl
import com.egobook.app.data.repository.NotificationRepositoryImpl
import com.egobook.app.domain.repository.CounselingRepository
import com.egobook.app.data.repository.QuestionRepositoryImpl
import com.egobook.app.domain.repository.DiaryRepository
import com.egobook.app.domain.repository.FriendsRepository
import com.egobook.app.domain.repository.LetterRepository
import com.egobook.app.domain.repository.NotificationRepository
import dagger.Binds
import com.egobook.app.domain.repository.QuestionRepository
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindCounselingRepository(impl: CounselingRepositoryImpl): CounselingRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    @Binds
    @Singleton
    abstract fun bindFriendsRepository(impl: FriendsRepositoryImpl): FriendsRepository

    @Binds
    @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(impl: DiaryRepositoryImpl): DiaryRepository

    @Binds
    @Singleton
    abstract fun bindLetterRepository(impl: LetterRepositoryImpl): LetterRepository
}
