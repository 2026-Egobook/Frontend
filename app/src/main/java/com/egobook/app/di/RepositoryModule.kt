package com.egobook.app.di

import com.egobook.app.data.repository.CounselingRepositoryImpl
import com.egobook.app.data.repository.FriendsRepositoryImpl
import com.egobook.app.data.repository.NotificationRepositoryImpl
import com.egobook.app.domain.repository.CounselingRepository
import com.egobook.app.data.repository.auth.AuthRepositoryImpl
import com.egobook.app.data.repository.QuestionRepositoryImpl
import com.egobook.app.data.repository.diary.DiaryRepositoryImpl
import com.egobook.app.domain.repository.FriendsRepository
import com.egobook.app.domain.repository.NotificationRepository
import com.egobook.app.domain.repository.auth.AuthRepository
import com.egobook.app.ui.shop.NetworkStoreRepository
import com.egobook.app.ui.shop.StoreRepository
import dagger.Binds
import com.egobook.app.domain.repository.QuestionRepository
import com.egobook.app.domain.repository.diary.DiaryRepository
import com.egobook.app.ui.home.repository.NetworkTendencyLevelService
import com.egobook.app.ui.home.repository.NetworkUserRepository
import com.egobook.app.ui.home.repository.UserActivityRepository
import com.egobook.app.ui.home.repository.UserRepository
import com.egobook.app.ui.home.repository.UserTendencyRepository
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
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(impl: DiaryRepositoryImpl): DiaryRepository


    @Binds
    @Singleton
    abstract fun bindStoreRepository(impl: NetworkStoreRepository): StoreRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: NetworkUserRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindUserTendencyRepository(impl: NetworkUserRepository): UserTendencyRepository

    @Binds
    @Singleton
    abstract fun bindActivityRecordRepository(impl: NetworkUserRepository): UserActivityRepository
}
