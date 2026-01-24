package com.egobook.app.domain.repository

import com.egobook.app.domain.model.PraiseMessage
import com.egobook.app.domain.model.WeeklyReport
import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.Statistics
import com.egobook.app.domain.model.WeeklyReportStyle

interface CounselingRepository {
    suspend fun getDailyPraise(): Result<List<PraiseMessage>>
    suspend fun getWeeklyReport(): Result<List<WeeklyReport>>
    suspend fun getWeeklyReportStyle(): Result<WeeklyReportStyle>
    suspend fun updateWeeklyReportStyle(reportStyle: ReportStyle): Result<ReportStyle>
    suspend fun getStatistics(): Result<Statistics>
}