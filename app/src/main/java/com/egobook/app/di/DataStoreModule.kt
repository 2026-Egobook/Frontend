package com.egobook.app.di

import android.content.Context
import com.egobook.app.data.local.UserTokenStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideUserTokenStorage(
        @ApplicationContext context: Context
    ): UserTokenStorage = TODO()

}