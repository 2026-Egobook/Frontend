package com.egobook.app.data.model.counseling

import com.egobook.app.domain.model.WeeklyReport
import com.egobook.app.domain.model.WeeklyReportContent

data class WeeklyReportResponse(
    val id: Long,
    val date: String,
    val content: WeeklyReportContentResponse
)

data class WeeklyReportContentResponse(
    val analysis: String,
    val praisePoint: String,
    val improvement: String,
    val management: String,
    val encouragement: String
)

fun WeeklyReportResponse.toDomain(): WeeklyReport = WeeklyReport(
    id = id,
    date = date,
    content = WeeklyReportContent(
        analysis = content.analysis,
        praisePoint = content.praisePoint,
        improvement = content.improvement,
        management = content.management,
        encouragement = content.encouragement
    )
)

