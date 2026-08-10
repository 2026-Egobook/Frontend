package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.push.FcmTokenRequest
import com.google.gson.JsonElement
import retrofit2.http.Body
import retrofit2.http.PATCH

interface PushApiService {
    // data 필드가 문자열/객체/null 어느 것이든 Gson이 파싱 가능하도록 JsonElement? 사용
    @PATCH("/users/fcm-token")
    suspend fun updateFcmToken(
        @Body request: FcmTokenRequest,
    ): ApiResponse<JsonElement?>
}
