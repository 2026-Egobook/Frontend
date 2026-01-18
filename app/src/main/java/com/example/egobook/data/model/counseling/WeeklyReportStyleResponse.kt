package com.example.egobook.data.model.counseling

import com.example.egobook.domain.model.ReportStyle
import com.example.egobook.domain.model.WeeklyReportStyle

data class WeeklyReportStyleResponse(
    val type: ReportStyle
)

fun WeeklyReportStyleResponse.toDomain(): WeeklyReportStyle = WeeklyReportStyle(
    type = type
)
