package com.egobook.app.domain.repository

import androidx.paging.PagingData
import com.egobook.app.domain.model.PraiseMessage
import com.egobook.app.domain.model.WeeklyReport
import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.Statistics
import com.egobook.app.domain.model.WeeklyReportStyle
import com.egobook.app.domain.model.counseling.PraiseDailyItem
import kotlinx.coroutines.flow.Flow

interface CounselingRepository {
    fun getDailyPraise(size: Int): Flow<PagingData<PraiseDailyItem>>
    suspend fun getWeeklyReport(): Result<List<WeeklyReport>>
    suspend fun getWeeklyReportStyle(): Result<WeeklyReportStyle>
    suspend fun updateWeeklyReportStyle(reportStyle: ReportStyle): Result<ReportStyle>
    suspend fun getStatistics(): Result<Statistics>
}