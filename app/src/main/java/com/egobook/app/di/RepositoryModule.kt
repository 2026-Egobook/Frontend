package com.egobook.app.di

import com.egobook.app.data.repository.CounselingRepositoryImpl
import com.egobook.app.data.repository.NotificationRepositoryImpl
import com.egobook.app.data.repository.FriendsRepositoryImpl
import com.egobook.app.domain.repository.CounselingRepository
import com.egobook.app.domain.repository.NotificationRepository
import com.egobook.app.domain.repository.FriendsRepository
import dagger.Binds
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

}