package com.example.egobook_frontent.data.model.notification

import com.example.egobook_frontent.domain.model.Notification

data class NotificationResponse(
    val isDailyPraiseEnabled: Boolean,
    val isWeeklyReportEnabled: Boolean
)

fun NotificationResponse.toDomain(): Notification = Notification(
    isDailyPraiseEnabled = isDailyPraiseEnabled,
    isWeeklyReportEnabled = isWeeklyReportEnabled
)
