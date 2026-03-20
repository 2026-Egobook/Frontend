package com.egobook.app.di.module

import com.egobook.app.domain.repository.diary.FakeDiaryRepository
import com.egobook.app.domain.repository.auth.AuthRepository
import com.egobook.app.domain.repository.diary.CalenderRepository
import com.egobook.app.domain.repository.diary.DiaryRepository
import com.egobook.app.domain.usecase.CalenderUseCases
import com.egobook.app.domain.usecase.GetCalender
import com.egobook.app.domain.usecase.authusecase.AuthUseCases
import com.egobook.app.domain.usecase.authusecase.GoogleAutoLogin
import com.egobook.app.domain.usecase.authusecase.GoogleLogin
import com.egobook.app.domain.usecase.authusecase.GoogleSignUp
import com.egobook.app.domain.usecase.authusecase.GuestLogin
import com.egobook.app.domain.usecase.authusecase.GuestReLogin
import com.egobook.app.domain.usecase.diaryusecase.AddDiary
import com.egobook.app.domain.usecase.diaryusecase.DeleteDiary
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
import com.egobook.app.domain.usecase.diaryusecase.ExportDiary
import com.egobook.app.domain.usecase.diaryusecase.GetDailyCount
import com.egobook.app.domain.usecase.diaryusecase.GetDiaries
import com.egobook.app.domain.usecase.diaryusecase.GetDiary
import com.egobook.app.domain.usecase.diaryusecase.UpdateDiary
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideDiaryUseCases(repository: DiaryRepository): DiaryUseCases {

        return DiaryUseCases(
            getDiaries = GetDiaries(repository),
            getDiary = GetDiary(repository),
            addDiary = AddDiary(repository),
            updateDiary = UpdateDiary(repository),
            deleteDiary = DeleteDiary(repository),
            getDailyCount = GetDailyCount(repository),
            exportDiary = ExportDiary(repository)
        )

    }

    @Provides
    @Singleton
    fun provideAuthUseCases(repository: AuthRepository): AuthUseCases {
        return AuthUseCases(
            googleSignUp = GoogleSignUp(repository),
            googleAutoLogin = GoogleAutoLogin(repository),
            googleLogin = GoogleLogin(repository),
            guestLogin = GuestLogin(repository),
            guestReLogin = GuestReLogin(repository)
        )
    }

    @Provides
    @Singleton
    fun provideCalenderUseCases(repository: CalenderRepository): CalenderUseCases {
        return CalenderUseCases(
            getCalender = GetCalender(repository)
        )
    }

}