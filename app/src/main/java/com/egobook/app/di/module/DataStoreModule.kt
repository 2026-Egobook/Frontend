package com.egobook.app.di.module

import android.content.Context
import com.egobook.app.data.local.PushPreferenceStorage
import com.egobook.app.data.local.UserInfoStorage
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
    ): UserInfoStorage = UserInfoStorage(context)

    @Provides
    @Singleton
    fun providePushPreferenceStorage(
        @ApplicationContext context: Context
    ): PushPreferenceStorage = PushPreferenceStorage(context)

}