package com.egobook.app.di

import com.egobook.app.domain.repository.DiaryRepository
import com.egobook.app.domain.repository.auth.AuthRepository
import com.egobook.app.domain.usecase.authusecase.AuthUseCases
import com.egobook.app.domain.usecase.authusecase.GoogleSignUp
import com.egobook.app.domain.usecase.diaryusecase.AddDiary
import com.egobook.app.domain.usecase.diaryusecase.DeleteDiary
import com.egobook.app.domain.usecase.diaryusecase.DiaryUseCases
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
            deleteDiary = DeleteDiary(repository)
        )

    }

    @Provides
    @Singleton
    fun provideAuthUseCases(repository: AuthRepository): AuthUseCases {
        return AuthUseCases(
            googleSignUp = GoogleSignUp(repository)
        )
    }

}