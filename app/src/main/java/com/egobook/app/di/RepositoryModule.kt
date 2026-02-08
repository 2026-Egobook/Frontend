package com.egobook.app.di

import com.egobook.app.data.repository.CounselingRepositoryImpl
import com.egobook.app.data.repository.diary.FakeDiaryRepositoryImpl
import com.egobook.app.data.repository.FriendsRepositoryImpl
import com.egobook.app.data.repository.NotificationRepositoryImpl
import com.egobook.app.domain.repository.CounselingRepository
import com.egobook.app.data.repository.auth.AuthRepositoryImpl
import com.egobook.app.data.repository.QuestionRepositoryImpl
import com.egobook.app.domain.repository.diary.FakeDiaryRepository
import com.egobook.app.domain.repository.FriendsRepository
import com.egobook.app.domain.repository.NotificationRepository
import com.egobook.app.domain.repository.auth.AuthRepository
import com.egobook.app.ui.shop.NetworkStoreRepository
import com.egobook.app.ui.shop.StoreRepository
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
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    // TODO: 백엔드 API 연동 시:
    //  1. DiaryRepository 인터페이스 생성
    //  2. DiaryRepositoryImpl 구현 (ApiService 사용)
    //  3. 이 바인딩을 DiaryRepositoryImpl -> DiaryRepository로 변경
    //  4. 모든 UseCase의 FakeDiaryRepository -> DiaryRepository로 변경
    @Binds
    @Singleton
    abstract fun bindFakeDiaryRepository(impl: FakeDiaryRepositoryImpl): FakeDiaryRepository

    @Binds
    @Singleton
    abstract fun bindStoreRepository(impl: NetworkStoreRepository): StoreRepository
}