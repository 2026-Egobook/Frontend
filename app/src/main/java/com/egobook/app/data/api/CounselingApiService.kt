package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.counseling.DailyAndWeeklyNotificationResponse
import com.egobook.app.data.model.counseling.DailyPraiseResponse
import com.egobook.app.data.model.counseling.DailyPraisesResponse
import com.egobook.app.data.model.counseling.StatisticsResponse
import com.egobook.app.data.model.counseling.WeeklyReportResponse
import com.egobook.app.data.model.counseling.WeeklyReportStyleResponse
import com.egobook.app.domain.model.ReportStyle
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CounselingApiService {
    @GET("/ego-room/praise/daily")
    suspend fun fetchDailyPraises(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<DailyPraisesResponse>

    @GET("/ego-room/praise/daily/{date}")
    suspend fun fetchDailyPraiseByDate(
        @Path("date") date: String
    ): Response<DailyPraiseResponse>

    @GET("/ego-room/ai/toggle")
    suspend fun fetchDailyAndWeeklyNotification(): ApiResponse<DailyAndWeeklyNotificationResponse>

    @GET("api/reports/weekly")
    suspend fun fetchWeeklyReports(): Response<List<WeeklyReportResponse>>

    @GET("api/reports/weekly/style")
    suspend fun fetchWeeklyReportStyle(): Response<WeeklyReportStyleResponse>

    @POST("api/reports/weekly/style")
    suspend fun updateWeeklyReportStyle(@Query("style") reportStyle: ReportStyle): Response<Unit>

    @GET("api/statistics")
    suspend fun fetchStatistics(): Response<StatisticsResponse>
}