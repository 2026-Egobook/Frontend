package com.example.egobook_frontent.data.api

import com.example.egobook_frontent.data.model.counseling.PraiseMessageResponse
import com.example.egobook_frontent.data.model.counseling.WeeklyReportResponse
import com.example.egobook_frontent.data.model.counseling.WeeklyReportStyleResponse
import com.example.egobook_frontent.domain.model.ReportStyle
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface CounselingService {
    @GET("api/praise/daily")
    suspend fun fetchDailyPraise(): Response<List<PraiseMessageResponse>>

    @GET("api/reports/weekly")
    suspend fun fetchWeeklyReports(): Response<List<WeeklyReportResponse>>

    @GET("api/reports/weekly/style")
    suspend fun fetchWeeklyReportStyle(): Response<WeeklyReportStyleResponse>

    @POST("api/reports/weekly/style")
    suspend fun updateWeeklyReportStyle(@Query("style") reportStyle: ReportStyle): Response<Unit>
}