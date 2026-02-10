package com.egobook.app.data.model.counseling

import com.egobook.app.domain.model.counseling.WeeklyReportItem
import com.google.gson.annotations.SerializedName

data class WeeklyReportsResponse(
    @SerializedName("content")
    val content: List<WeeklyReportsContentResponse>,
    @SerializedName("page")
    val page: Int,
    @SerializedName("size")
    val size: Int,
    @SerializedName("hasNext")
    val hasNext: Boolean
)

data class WeeklyReportsContentResponse(
    @SerializedName("id")
    val id: Long,
    @SerializedName("startDate")
    val startDate: String,
    @SerializedName("endDate")
    val endDate: String,
    @SerializedName("isRead")
    val isRead: Boolean,
    @SerializedName("isLocked")
    val isLocked: Boolean
)

fun WeeklyReportsContentResponse.toDomain(): WeeklyReportItem = WeeklyReportItem(
    id = id,
    startDate = startDate,
    endDate = endDate,
    isRead = isRead,
    isLocked = isLocked
)

