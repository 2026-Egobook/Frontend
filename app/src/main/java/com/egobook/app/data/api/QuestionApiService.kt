package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.square.question.TodayQuestionResponse
import retrofit2.http.GET

interface QuestionApiService {
    @GET("/questions/today")
    suspend fun fetchTodayQuestion(): ApiResponse<TodayQuestionResponse>
}