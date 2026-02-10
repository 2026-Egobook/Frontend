package com.egobook.app.domain.model.counseling

data class WeeklyReportItem(
    val id: Long,
    val startDate: String,
    val endDate: String,
    val isRead: Boolean,
    val isLocked: Boolean
)