package com.egobook.app.ui.counseling.model

import com.egobook.app.domain.model.counseling.WeeklyReportDetail

data class WeeklyReportDetailModel(
    val startDate: String,
    val endDate: String,
    val summary: String,
    val praisePoints: String,
    val improvementPoints: String,
    val managementAdvice: String,
    val supportMessage: String,
    val isRead: Boolean
)

fun WeeklyReportDetail.toPresentation() = WeeklyReportDetailModel(
    startDate = startDate,
    endDate = endDate,
    summary = summary,
    praisePoints = praisePoints,
    improvementPoints = improvementPoints,
    managementAdvice = managementAdvice,
    supportMessage = supportMessage,
    isRead = isRead
)