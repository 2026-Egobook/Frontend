package com.example.egobook_frontent.di

import com.example.egobook_frontent.data.repository.CounselingRepositoryImpl
import com.example.egobook_frontent.data.repository.NotificationRepositoryImpl
import com.example.egobook_frontent.data.repository.SquareRepositoryImpl
import com.example.egobook_frontent.domain.repository.CounselingRepository
import com.example.egobook_frontent.domain.repository.NotificationRepository
import com.example.egobook_frontent.domain.repository.SquareRepository
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
    abstract fun bindSquareRepository(impl: SquareRepositoryImpl): SquareRepository

}