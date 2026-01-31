package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.square.letter.SendLetterRequest
import com.egobook.app.data.model.square.letter.SendLetterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LetterApiService {
    @POST("/plaza/letters")
    suspend fun sendLetter(@Body request: SendLetterRequest): ApiResponse<SendLetterResponse>
}