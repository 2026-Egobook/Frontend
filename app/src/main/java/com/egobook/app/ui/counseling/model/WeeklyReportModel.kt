package com.egobook.app.ui.counseling.model

import android.os.Parcelable
import com.egobook.app.domain.model.counseling.WeeklyReport
import kotlinx.parcelize.Parcelize

@Parcelize
data class WeeklyReportModel(
    val id: Long,
    val startDate: String, // 사용 완료
    val endDate: String, // 사용 완료
    val isRead: Boolean,
    val isLocked: Boolean // 사용 완료
): Parcelable

fun WeeklyReport.toPresentation(): WeeklyReportModel = WeeklyReportModel(
    id = id,
    startDate = startDate,
    endDate = endDate,
    isRead = isRead,
    isLocked = isLocked
)
