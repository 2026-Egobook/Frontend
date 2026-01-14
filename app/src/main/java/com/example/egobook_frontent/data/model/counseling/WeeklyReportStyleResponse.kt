package com.example.egobook_frontent.data.model.counseling

import com.example.egobook_frontent.domain.model.ReportStyle
import com.example.egobook_frontent.domain.model.WeeklyReportStyle

data class WeeklyReportStyleResponse(
    val type: ReportStyle
)

fun WeeklyReportStyleResponse.toDomain(): WeeklyReportStyle = WeeklyReportStyle(
    type = type
)
