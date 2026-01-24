package com.egobook.app.ui.counseling.model

import android.os.Parcelable
import com.egobook.app.domain.model.WeeklyReport
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeeklyReportModel(
    val id: Long,
    val date: String,
    val content: WeeklyReportContentModel
): Parcelable

@Parcelize
data class WeeklyReportContentModel(
    val analysis: String,
    val praisePoint: String,
    val improvement: String,
    val management: String,
    val encouragement: String
): Parcelable

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
