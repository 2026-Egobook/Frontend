package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.diary.response.CalenderData
import retrofit2.http.GET
import retrofit2.http.Query

interface CalenderApiService {
    //선택한 연도/월의 날짜별로 가장 많이 기록된 대표 감정 단계를 확인
    @GET("/diaries/calendar")
    suspend fun getCalender(
        @Query("month") month: String,
    ): ApiResponse<CalenderData>
}