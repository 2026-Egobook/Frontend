package com.egobook.app

import android.app.Application
import com.egobook.app.push.NotificationChannels
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(AppDebugTree())
        }

        NotificationChannels.createAll(this)
    }

    private class AppDebugTree : Timber.DebugTree() {
        override fun createStackElementTag(element: StackTraceElement): String {
            return "TIMBER"
        }
    }
}
