package com.egobook.app.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.egobook.app.domain.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "egobook_auth")

/**
 * 사용자 인증 토큰 및 디바이스 정보를 저장하고 관리하는 로컬 저장소
 */
@Singleton
class UserInfoStorage @Inject constructor(
    private val context: Context
) {
    private val dataStore = context.dataStore


    /**
     * LoginType 저장
     */
    suspend fun saveLoginType(type: LoginType) {
        dataStore.edit { preferences ->
            preferences[LOGIN_TYPE] = type.name
        }
    }

    /**
     * LoginType 읽기
     */
    fun getLoginType(): Flow<LoginType?> {
        return dataStore.data.map { preferences ->
            preferences[LOGIN_TYPE]?.let {
                try {
                    LoginType.valueOf(it)
                } catch (e: IllegalArgumentException) {
                    null
                }
            }
        }
    }

    /**
     * Access Token 저장
     */
    suspend fun saveAccessToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = token
        }
    }

    /**
     * Access Token 읽기
     */
    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_ACCESS_TOKEN]
        }
    }

    /**
     * Refresh Token 저장
     */
    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_REFRESH_TOKEN] = token
        }
    }

    /**
     * Refresh Token 읽기
     */
    fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_REFRESH_TOKEN]
        }
    }

    /**
     * Recover Token 저장
     */
    suspend fun saveRecoverToken(token: String) {
        dataStore.edit { preferences ->
            preferences[KEY_RECOVER_TOKEN] = token
        }
    }

    /**
     * Recover Token 읽기
     */
    fun getRecoverToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_RECOVER_TOKEN]
        }
    }

    /**
     * Device UID 저장
     */
    suspend fun saveDeviceUid(uid: String) {
        dataStore.edit { preferences ->
            preferences[KEY_DEVICE_UID] = uid
        }
    }

    /**
     * Device UID 읽기
     */
    fun getDeviceUid(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[KEY_DEVICE_UID]
        }
    }

    /**
     * 모든 토큰과 디바이스 정보 저장
     */
    suspend fun saveAllTokens(
        accessToken: String,
        refreshToken: String,
        idToken: String? = null,
        recoverToken: String? = null,
    ) {
        dataStore.edit { preferences ->
            preferences[KEY_ACCESS_TOKEN] = accessToken
            preferences[KEY_REFRESH_TOKEN] = refreshToken
            recoverToken?.let { preferences[KEY_RECOVER_TOKEN] = it }
        }
    }

    /**
     * 모든 데이터 삭제 (로그아웃 시 사용)
     */
    suspend fun clearAll() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    enum class LoginType {
        GOOGLE, GUEST
    }

    //저장할 키값 종류 정의
    companion object {

        private val LOGIN_TYPE = stringPreferencesKey("login_type")
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val KEY_RECOVER_TOKEN = stringPreferencesKey("recover_token")
        private val KEY_DEVICE_UID = stringPreferencesKey("device_uid")


    }
}
