package com.egobook.app.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Google ID Token을 저장하고 관리하는 클래스
 */
@Singleton
class UserTokenStorage @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    /**
     * Google ID Token 저장
     */
    fun saveGoogleIdToken(token: String) {
        prefs.edit().putString(KEY_GOOGLE_ID_TOKEN, token).apply()
    }

    companion object {
        private const val PREF_NAME = "egobook_auth"
        private const val KEY_GOOGLE_ID_TOKEN = "google_id_token"
    }
}
