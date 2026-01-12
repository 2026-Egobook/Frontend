package com.example.egobook_frontent.data.api

import com.example.egobook_frontent.data.model.PraiseMessageResponse
import com.example.egobook_frontent.data.model.WeeklyReportResponse
import retrofit2.Response
import retrofit2.http.GET

interface CounselingApiService {
    @GET("api/praise/daily")
    suspend fun fetchDailyPraise(): Response<List<PraiseMessageResponse>>

    @GET("api/reports/weekly")
    suspend fun fetchWeeklyReports(): Response<List<WeeklyReportResponse>>
}