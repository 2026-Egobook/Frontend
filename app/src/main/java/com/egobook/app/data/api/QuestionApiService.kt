package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.square.question.TodayAnswerRequest
import com.egobook.app.data.model.square.question.TodayQuestionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface QuestionApiService {
    @GET("/questions/today")
    suspend fun fetchTodayQuestion(): ApiResponse<TodayQuestionResponse>

    @POST("/questions/answers")
    suspend fun submitTodayAnswer(@Body answer: TodayAnswerRequest): ApiResponse<Unit>
}