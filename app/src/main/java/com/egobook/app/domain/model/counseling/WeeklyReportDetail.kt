package com.egobook.app.domain.model.counseling

data class WeeklyReportDetail(
    val startDate: String,
    val endDate: String,
    val summary: String,
    val praisePoints: String,
    val improvementPoints: String,
    val managementAdvice: String,
    val supportMessage: String,
    val isRead: Boolean
)
