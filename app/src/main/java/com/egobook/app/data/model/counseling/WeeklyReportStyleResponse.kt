package com.egobook.app.data.model.counseling

import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.WeeklyReportStyle

data class WeeklyReportStyleResponse(
    val type: ReportStyle
)

fun WeeklyReportStyleResponse.toDomain(): WeeklyReportStyle = WeeklyReportStyle(
    type = type
)
