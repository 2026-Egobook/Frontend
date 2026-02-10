package com.egobook.app.data.model.counseling

import com.egobook.app.domain.model.counseling.WeeklyReportDetail
import com.google.gson.annotations.SerializedName

data class WeeklyReportResponse(
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("summary")
    val summary: String,
    @SerializedName("praisePoints")
    val praisePoints: String,
    @SerializedName("improvementPoints")
    val improvementPoints: String,
    @SerializedName("managementAdvice")
    val managementAdvice: String,
    @SerializedName("supportMessage")
    val supportMessage: String,
    @SerializedName("isRead")
    val isRead: Boolean
)

fun WeeklyReportResponse.toDomain() = WeeklyReportDetail(
    startDate = startDate,
    endDate = endDate,
    summary = summary,
    praisePoints = praisePoints,
    improvementPoints = improvementPoints,
    managementAdvice = managementAdvice,
    supportMessage = supportMessage,
    isRead = isRead
)