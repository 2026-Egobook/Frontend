package com.example.egobook_frontent.ui.counseling.model

import com.example.egobook_frontent.domain.model.WeeklyReport

data class WeeklyReportModel(
    val id: Long,
    val date: String,
    val content: WeeklyReportContentModel
)

data class WeeklyReportContentModel(
    val analysis: String,
    val praisePoint: String,
    val improvement: String,
    val management: String,
    val encouragement: String
)

fun WeeklyReport.toPresentation(): WeeklyReportModel = WeeklyReportModel(
    id = id,
    date = date,
    content = WeeklyReportContentModel(
        analysis = content.analysis,
        praisePoint = content.praisePoint,
        improvement = content.improvement,
        management = content.management,
        encouragement = content.encouragement
    )
)
