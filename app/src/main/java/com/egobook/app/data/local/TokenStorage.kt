package com.egobook.app.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Google ID Token을 저장하고 관리하는 클래스
 */
class TokenStorage private constructor(context: Context) {
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
        
        @Volatile
        private var INSTANCE: TokenStorage? = null
        
        fun getInstance(context: Context): TokenStorage {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: TokenStorage(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
