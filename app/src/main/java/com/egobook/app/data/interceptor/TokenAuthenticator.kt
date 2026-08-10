package com.egobook.app.data.interceptor

import android.content.Context
import android.content.Intent
import com.egobook.app.data.api.AuthApiService
import com.egobook.app.data.local.PushPreferenceStorage
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.data.model.auth.AccessTokenRequest
import com.egobook.app.di.qualifier.AuthRetrofit
import com.egobook.app.ui.login.view.LoginActivity
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val userInfoStorage: UserInfoStorage,
    private val pushPreferenceStorage: PushPreferenceStorage,
    private val authApiService: AuthApiService
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // 이미 재시도한 요청이면 더 이상 재시도하지 않음
        if (response.request.header("Authorization") != null && 
            responseCount(response) >= 3) {
            Timber.w("토큰 갱신 재시도 횟수 초과")
            return null
        }

        Timber.d("401 응답 감지, 토큰 갱신 시작")

        return runBlocking {
            try {
                // 액세스,리프레시 토큰 가져오기
                val accessToken = userInfoStorage.getAccessToken().first()
                val refreshToken = userInfoStorage.getRefreshToken().first()
                
                if (accessToken.isNullOrEmpty()) {
                    Timber.e("로컬에 액세스 토큰이 없음")
                    handleLogout()
                    return@runBlocking null
                }
                
                if (refreshToken.isNullOrEmpty()) {
                    Timber.e("로컬에 리프레시 토큰이 없음")
                    handleLogout()
                    return@runBlocking null
                }

                // 액세스 토큰 갱신 API 호출
                Timber.d("토큰 갱신 API 호출 시작")
                val tokenResponse = authApiService.getAccessToken(
                    AccessTokenRequest(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                )
                
                Timber.d("토큰 갱신 API 응답: 코드=${tokenResponse.code}, 메시지=${tokenResponse.message}")

                if (tokenResponse.code == "SUCCESS") {
                    val newAccessToken = tokenResponse.data.accessToken
                    val newRefreshToken = tokenResponse.data.refreshToken
                    
                    // 새 토큰들을 DataStore에 저장
                    userInfoStorage.saveAllTokens(
                        accessToken = newAccessToken,
                        refreshToken = newRefreshToken
                    )
                    
                    Timber.d("액세스 토큰 갱신 성공")
                    
                    // 새 토큰으로 재요청
                    Timber.d("갱신된 액세스 토큰으로 재요청")
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()

                } else {
                    // 리프레시 토큰 갱신 실패 -> 리프레시 토큰 만료로 판단
                    Timber.e("리프레시 토큰 갱신 실패: ${tokenResponse.code}, 로그아웃 처리")
                    handleLogout()
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "토큰 갱신 중 예외 발생, 로그아웃 처리")
                handleLogout()
                null
            }
        }
    }

    /**
     * 로그아웃 처리: 토큰 클리어 후 로그인 화면으로 이동
     */
    private fun handleLogout() {
        Timber.d("로그아웃 처리 시작")

        runBlocking {
            userInfoStorage.clearAll()   // 토큰, id, 이메일 등등 싹다 삭제
            // 다른 계정으로 재로그인했을 때 같은 토큰이라는 이유로 등록이 생략되지 않도록 이력 삭제
            pushPreferenceStorage.clearLastRegisteredToken()
        }

        val intent = Intent(context, LoginActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        context.startActivity(intent)

        Timber.d("로그인 화면으로 이동")
    }

    /**
     * 재시도 횟수 계산
     */
    private fun responseCount(response: Response): Int {
        var count = 1
        var priorResponse = response.priorResponse
        while (priorResponse != null) {
            count++
            priorResponse = priorResponse.priorResponse
        }
        return count
    }
}