package com.example.egobook.domain.repository

import com.example.egobook.domain.model.PraiseMessage
import com.example.egobook.domain.model.WeeklyReport
import com.example.egobook.domain.model.ReportStyle
import com.example.egobook.domain.model.Statistics
import com.example.egobook.domain.model.WeeklyReportStyle

interface CounselingRepository {
    suspend fun getDailyPraise(): Result<List<PraiseMessage>>
    suspend fun getWeeklyReport(): Result<List<WeeklyReport>>
    suspend fun getWeeklyReportStyle(): Result<WeeklyReportStyle>
    suspend fun updateWeeklyReportStyle(reportStyle: ReportStyle): Result<ReportStyle>
    suspend fun getStatistics(): Result<Statistics>
}