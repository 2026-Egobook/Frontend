package com.example.egobook_frontent.ui.counseling.model

import com.example.egobook_frontent.domain.model.ReportStyle
import com.example.egobook_frontent.domain.model.WeeklyReportStyle

data class WeeklyReportStyleModel(
    val type: ReportStyle
)

fun WeeklyReportStyle.toPresentation(): WeeklyReportStyleModel = WeeklyReportStyleModel(
    type = type
)


