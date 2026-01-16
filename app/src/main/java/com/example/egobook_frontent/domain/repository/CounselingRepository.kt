package com.example.egobook_frontent.domain.repository

import com.example.egobook_frontent.domain.model.PraiseMessage
import com.example.egobook_frontent.domain.model.WeeklyReport
import com.example.egobook_frontent.domain.model.ReportStyle
import com.example.egobook_frontent.domain.model.Statistics
import com.example.egobook_frontent.domain.model.WeeklyReportStyle

interface CounselingRepository {
    suspend fun getDailyPraise(): Result<List<PraiseMessage>>
    suspend fun getWeeklyReport(): Result<List<WeeklyReport>>
    suspend fun getWeeklyReportStyle(): Result<WeeklyReportStyle>
    suspend fun updateWeeklyReportStyle(reportStyle: ReportStyle): Result<ReportStyle>
    suspend fun getStatistics(): Result<Statistics>
}