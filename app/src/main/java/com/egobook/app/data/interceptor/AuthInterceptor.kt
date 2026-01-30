package com.egobook.app.data.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer ${USER_ACCESS_TOKEN}")
            .build()
        return chain.proceed(newRequest)
    }

    companion object {
        const val USER_ACCESS_TOKEN = ""
    }
}