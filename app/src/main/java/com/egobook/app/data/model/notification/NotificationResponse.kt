package com.egobook.app.data.model.notification

import com.egobook.app.domain.model.Notification

data class NotificationResponse(
    val isDailyPraiseEnabled: Boolean,
    val isWeeklyReportEnabled: Boolean
)

fun NotificationResponse.toDomain(): Notification = Notification(
    isDailyPraiseEnabled = isDailyPraiseEnabled,
    isWeeklyReportEnabled = isWeeklyReportEnabled
)
