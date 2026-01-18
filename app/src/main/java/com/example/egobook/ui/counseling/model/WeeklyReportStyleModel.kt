package com.example.egobook.ui.counseling.model

import com.example.egobook.domain.model.ReportStyle
import com.example.egobook.domain.model.WeeklyReportStyle

data class WeeklyReportStyleModel(
    val type: ReportStyle
)

fun WeeklyReportStyle.toPresentation(): WeeklyReportStyleModel = WeeklyReportStyleModel(
    type = type
)


