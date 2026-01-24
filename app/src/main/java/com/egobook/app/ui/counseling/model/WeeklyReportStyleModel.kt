package com.egobook.app.ui.counseling.model

import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.WeeklyReportStyle

data class WeeklyReportStyleModel(
    val type: ReportStyle
)

fun WeeklyReportStyle.toPresentation(): WeeklyReportStyleModel = WeeklyReportStyleModel(
    type = type
)


