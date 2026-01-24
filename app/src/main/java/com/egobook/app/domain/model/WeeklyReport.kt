package com.egobook.app.domain.model

data class WeeklyReport(
    val id: Long,
    val date: String,
    val content: WeeklyReportContent
)

data class WeeklyReportContent(
    val analysis: String,
    val praisePoint: String,
    val improvement: String,
    val management: String,
    val encouragement: String
)
