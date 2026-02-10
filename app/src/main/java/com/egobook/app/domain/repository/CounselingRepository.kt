package com.egobook.app.domain.repository

import androidx.paging.PagingData
import com.egobook.app.domain.model.counseling.WeeklyReport
import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.Statistics
import com.egobook.app.domain.model.WeeklyReportStyle
import com.egobook.app.domain.model.counseling.DailyAndWeeklyNotification
import com.egobook.app.domain.model.counseling.DailyPraise
import com.egobook.app.domain.model.counseling.DailyPraiseDetail
import com.egobook.app.domain.model.counseling.WeeklyReportDetail
import com.egobook.app.domain.model.counseling.WeeklyReportUnlockType
import kotlinx.coroutines.flow.Flow

interface CounselingRepository {
    fun getDailyPraise(size: Int): Flow<PagingData<DailyPraise>>
    suspend fun getDailyPraiseByDate(date: String): Result<DailyPraiseDetail>
    fun getWeeklyReports(size: Int): Flow<PagingData<WeeklyReport>>
    suspend fun getWeeklyReportStyle(): Result<WeeklyReportStyle>
    suspend fun updateWeeklyReportStyle(reportStyle: ReportStyle): Result<ReportStyle>
    suspend fun getStatistics(): Result<Statistics>
    suspend fun getDailyAndWeeklyNotification(): Result<DailyAndWeeklyNotification>
    suspend fun updateDailyPraiseNotification(isEnabled: Boolean): Result<Boolean>
    suspend fun updateWeeklyReportNotification(isEnabled: Boolean): Result<Boolean>
    suspend fun getWeeklyReportByDate(startDate: String): Result<WeeklyReportDetail>
    suspend fun unlockWeeklyReport(startDate: String, unlockType: WeeklyReportUnlockType): Result<Unit>
}