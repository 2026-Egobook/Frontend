package com.egobook.app.domain.repository

import androidx.paging.PagingData
import com.egobook.app.domain.model.WeeklyReport
import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.Statistics
import com.egobook.app.domain.model.WeeklyReportStyle
import com.egobook.app.domain.model.counseling.DailyAndWeeklyNotification
import com.egobook.app.domain.model.counseling.DailyPraise
import com.egobook.app.domain.model.counseling.DailyPraiseDetail
import kotlinx.coroutines.flow.Flow

interface CounselingRepository {
    fun getDailyPraise(size: Int): Flow<PagingData<DailyPraise>>
    suspend fun getDailyPraiseByDate(date: String): Result<DailyPraiseDetail>
    suspend fun getWeeklyReport(): Result<List<WeeklyReport>>
    suspend fun getWeeklyReportStyle(): Result<WeeklyReportStyle>
    suspend fun updateWeeklyReportStyle(reportStyle: ReportStyle): Result<ReportStyle>
    suspend fun getStatistics(): Result<Statistics>
    suspend fun getDailyAndWeeklyNotification(): Result<DailyAndWeeklyNotification>
    suspend fun updateDailyPraiseNotification(isEnabled: Boolean): Result<Boolean>
    suspend fun updateWeeklyReportNotification(isEnabled: Boolean): Result<Boolean>
}