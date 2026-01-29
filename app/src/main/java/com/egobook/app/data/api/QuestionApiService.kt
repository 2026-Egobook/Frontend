package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.square.question.UserTodayQuestionAnswerResponse
import com.egobook.app.data.model.square.question.MyTodayQuestionAnswerResponse
import com.egobook.app.data.model.square.question.TodayAnswerRequest
import com.egobook.app.data.model.square.question.TodayQuestionResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

interface QuestionApiService {
    @GET("/questions/today")
    suspend fun fetchTodayQuestion(): ApiResponse<TodayQuestionResponse>

    @POST("/questions/answers")
    suspend fun submitTodayAnswer(@Body answer: TodayAnswerRequest): ApiResponse<Unit>

    @GET("/questions/answers/me/history")
    suspend fun fetchMyRepliesHistory(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): ApiResponse<MyTodayQuestionAnswerResponse>

    @GET("/questions/answers/friends")
    suspend fun fetchTodayFriendReplies(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): ApiResponse<UserTodayQuestionAnswerResponse>

    @GET("/questions/answers/all")
    suspend fun fetchTodayAllUserReplies(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): ApiResponse<UserTodayQuestionAnswerResponse>

    @PUT("/questions/answers")
    suspend fun updateTodayAnswer(
        @Body updatedAnswer: TodayAnswerRequest
    ): ApiResponse<Unit>

}
