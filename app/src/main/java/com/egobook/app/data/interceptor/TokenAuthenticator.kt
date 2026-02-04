package com.egobook.app.data.interceptor

import com.egobook.app.data.api.AuthApiService
import com.egobook.app.data.local.UserInfoStorage
import com.egobook.app.data.model.auth.AccessTokenRequest
import com.egobook.app.di.AuthRetrofit
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class TokenAuthenticator @Inject constructor(
    private val userInfoStorage: UserInfoStorage,
    @param:AuthRetrofit private val authApiService: AuthApiService
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
                // 리프레시 토큰 가져오기
                val refreshToken = userInfoStorage.getRefreshToken().first()
                
                if (refreshToken.isNullOrEmpty()) {
                    Timber.e("로컬에 리프레시 토큰이 없음")
                    return@runBlocking null
                }

                // 액세스 토큰 갱신 API 호출
                val tokenResponse = authApiService.getAccessToken(
                    AccessTokenRequest(refreshToken = refreshToken)
                )

                if (tokenResponse.isSuccessful && tokenResponse.body() != null) {
                    val newAccessToken = tokenResponse.body()!!.data.accessToken
                    
                    // 새 액세스 토큰을 DataStore에 저장
                    userInfoStorage.saveAccessToken(newAccessToken)
                    
                    Timber.d("토큰 갱신 성공")
                    
                    // 새 토큰으로 재요청
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    Timber.e("토큰 갱신 실패: ${tokenResponse.code()}")
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "토큰 갱신 중 예외 발생")
                null
            }
        }
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