package com.egobook.app.data.api

import com.egobook.app.data.model.square.letter.DetectAbusiveContentRequest
import com.egobook.app.data.model.square.letter.DetectAbusiveContentResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AIApiService {
    @POST("/detect")
    suspend fun detectAbusiveContent(@Body request: DetectAbusiveContentRequest): DetectAbusiveContentResponse
}