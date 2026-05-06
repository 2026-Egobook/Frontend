package com.egobook.app.data.api

import com.egobook.app.data.model.ApiResponse
import com.egobook.app.data.model.counseling.CounselingNotificationRequest
import com.egobook.app.data.model.counseling.DailyAndWeeklyNotificationResponse
import com.egobook.app.data.model.counseling.DailyPraiseResponse
import com.egobook.app.data.model.counseling.DailyPraisesResponse
import com.egobook.app.data.model.counseling.ReportStyleRequest
import com.egobook.app.data.model.counseling.StatisticsResponse
import com.egobook.app.data.model.counseling.WeeklyReportResponse
import com.egobook.app.data.model.counseling.WeeklyReportsResponse
import com.egobook.app.data.model.counseling.WeeklyReportStyleResponse
import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.counseling.WeeklyReportUnlockType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
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

    @PATCH("/ego-room/praise/daily")
    suspend fun updateDailyPraiseNotification(
        @Body request: CounselingNotificationRequest
    ): ApiResponse<Unit>

    @PATCH("/ego-room/counsel/weekly")
    suspend fun updateWeeklyReportNotification(
        @Body request: CounselingNotificationRequest
    ): ApiResponse<Unit>

    @GET("/ego-room/counsel/weekly")
    suspend fun fetchWeeklyReports(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ApiResponse<WeeklyReportsResponse>

    @GET("/ego-room/counsel/weekly/{startDate}")
    suspend fun fetchWeeklyReportByDate(
        @Path("startDate") startDate: String
    ): Response<WeeklyReportResponse>

    @PATCH("/ego-room/counsel/weekly/next-tone")
    suspend fun updateWeeklyReportStyle(
        @Body request: ReportStyleRequest
    ): Response<WeeklyReportStyleResponse>

    @POST("/ego-room/counsel/weekly/{startDate}/unlock")
    suspend fun unlockWeeklyReport(
        @Path("startDate") startDate: String,
        @Query("unlockType") unlockType: WeeklyReportUnlockType
    ): ApiResponse<Unit>


    @GET("/ego-room/counseling-tone")
    suspend fun fetchWeeklyReportStyle(): ApiResponse<ReportStyle>

    @GET("/ego-room/stats")
    suspend fun fetchStatistics(): Response<StatisticsResponse>
}
