package com.egobook.app.di

import com.egobook.app.data.repository.CounselingRepositoryImpl
import com.egobook.app.data.repository.FriendsRepositoryImpl
import com.egobook.app.data.repository.LetterRepositoryImpl
import com.egobook.app.data.repository.NotificationRepositoryImpl
import com.egobook.app.data.repository.QuestionRepositoryImpl
import com.egobook.app.data.repository.account.AccountRepositoryImpl
import com.egobook.app.data.repository.auth.AuthRepositoryImpl
import com.egobook.app.data.repository.diary.CalenderRepositoryImpl
import com.egobook.app.data.repository.diary.DiaryRepositoryImpl
import com.egobook.app.data.repository.push.PushTokenRepositoryImpl
import com.egobook.app.domain.repository.CounselingRepository
import com.egobook.app.domain.repository.FriendsRepository
import com.egobook.app.domain.repository.LetterRepository
import com.egobook.app.domain.repository.NotificationRepository
import com.egobook.app.domain.repository.QuestionRepository
import com.egobook.app.domain.repository.account.AccountRepository
import com.egobook.app.domain.repository.auth.AuthRepository
import com.egobook.app.domain.repository.diary.CalenderRepository
import com.egobook.app.domain.repository.diary.DiaryRepository
import com.egobook.app.domain.repository.push.PushTokenRepository
import com.egobook.app.ui.home.repository.HomeNotificationRepository
import com.egobook.app.ui.home.repository.NetworkHomeNotificationRepository
import com.egobook.app.ui.home.repository.NetworkNoticeRepository
import com.egobook.app.ui.home.repository.NoticeRepository
import com.egobook.app.ui.home.repository.NetworkUserRepository
import com.egobook.app.ui.home.repository.UserActivityRepository
import com.egobook.app.ui.home.repository.UserAdRepository
import com.egobook.app.ui.home.repository.UserPsychologyRepository
import com.egobook.app.ui.home.repository.UserRepository
import com.egobook.app.ui.home.repository.UserTendencyRepository
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

    @Binds
    @Singleton
    abstract fun bindQuestionRepository(impl: QuestionRepositoryImpl): QuestionRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAccountRepository(impl: AccountRepositoryImpl): AccountRepository

    @Binds
    @Singleton
    abstract fun bindPushTokenRepository(impl: PushTokenRepositoryImpl): PushTokenRepository

    @Binds
    @Singleton
    abstract fun bindDiaryRepository(impl: DiaryRepositoryImpl): DiaryRepository

    @Binds
    @Singleton
    abstract fun bindLetterRepository(impl: LetterRepositoryImpl): LetterRepository

    @Binds
    @Singleton
    abstract fun bindUserRepository(impl: NetworkUserRepository): UserRepository

    @Binds
    @Singleton
    abstract fun bindUserTendencyRepository(impl: NetworkUserRepository): UserTendencyRepository

    @Binds
    @Singleton
    abstract fun bindActivityRecordRepository(impl: NetworkUserRepository): UserActivityRepository

    @Binds
    @Singleton
    abstract fun bindAdRepository(impl: NetworkUserRepository): UserAdRepository
  
    @Binds
    @Singleton
    abstract fun bindPsychologyRepository(impl: NetworkUserRepository): UserPsychologyRepository
  
    @Binds
    @Singleton
    abstract fun bindHomeNotificationRepository(impl: NetworkHomeNotificationRepository): HomeNotificationRepository

    @Binds
    @Singleton
    abstract fun bindCalenderRepository(impl: CalenderRepositoryImpl): CalenderRepository

    @Binds
    @Singleton
    abstract fun bindNoticeRepository(impl: NetworkNoticeRepository): NoticeRepository
}
