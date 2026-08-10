package com.egobook.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.pushDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "egobook_notification",
)

/**
 * 푸시 알림 관련 로컬 상태를 저장하는 저장소.
 *
 * 알림 권한 요청 이력과 마지막으로 서버 등록에 성공한 FCM 토큰을 보관한다.
 * 로그아웃 시 초기화되면 안 되므로 인증 정보를 담는 [UserInfoStorage]와 파일을 분리한다.
 */
@Singleton
class PushPreferenceStorage
    @Inject
    constructor(
        private val context: Context,
    ) {
        private val dataStore = context.pushDataStore

        suspend fun hasRequestedPermissionBefore(): Boolean =
            dataStore.data
                .map { preferences -> preferences[KEY_HAS_REQUESTED_PERMISSION] ?: false }
                .first()

        suspend fun markPermissionRequested() {
            dataStore.edit { preferences ->
                preferences[KEY_HAS_REQUESTED_PERMISSION] = true
            }
        }

        suspend fun getLastRegisteredToken(): String? =
            dataStore.data
                .map { preferences -> preferences[KEY_LAST_REGISTERED_TOKEN] }
                .first()

        suspend fun saveLastRegisteredToken(token: String) {
            dataStore.edit { preferences ->
                preferences[KEY_LAST_REGISTERED_TOKEN] = token
            }
        }

        /**
         * 로그아웃 시 호출한다.
         * 다른 계정으로 로그인했을 때 같은 토큰이라는 이유로 등록이 생략되지 않도록 이력을 지운다.
         */
        suspend fun clearLastRegisteredToken() {
            dataStore.edit { preferences ->
                preferences.remove(KEY_LAST_REGISTERED_TOKEN)
            }
        }

        companion object {
            private val KEY_HAS_REQUESTED_PERMISSION =
                booleanPreferencesKey("has_requested_post_notifications")
            private val KEY_LAST_REGISTERED_TOKEN =
                stringPreferencesKey("last_registered_fcm_token")
        }
    }
