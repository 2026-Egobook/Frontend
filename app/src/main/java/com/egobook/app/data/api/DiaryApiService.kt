package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.diary.response.DiariesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DiaryApiService {

    //일기 목록 불러오기
    @GET("/diaries")
    suspend fun getDiaries(
        @Query("date") date: String,
        @Query("type") type: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 10
    ): ApiResponse<DiariesResponse>


}