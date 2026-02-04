package com.egobook.app.data.interceptor

import com.egobook.app.data.local.UserInfoStorage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val userInfoStorage: UserInfoStorage
): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // DataStore에서 액세스 토큰 가져오기
        val accessToken = runBlocking {
            userInfoStorage.getAccessToken().first()
        }
        
        // 토큰이 있으면 Authorization 헤더 추가
        val newRequest = if (!accessToken.isNullOrEmpty()) {
            Timber.d("Authorization 헤더 추가: ${originalRequest.url}")
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()
        } else {
            Timber.w("액세스 토큰 없음: ${originalRequest.url}")
            originalRequest
        }
        
        return chain.proceed(newRequest)
    }
}