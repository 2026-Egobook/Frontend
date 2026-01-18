package com.example.egobook.ui.notification.model

import com.example.egobook.domain.model.Notification

data class NotificationModel(
    val isDailyPraiseEnabled: Boolean,
    val isWeeklyReportEnabled: Boolean
)

fun Notification.toPresentation(): NotificationModel = NotificationModel(
    isDailyPraiseEnabled = isDailyPraiseEnabled,
    isWeeklyReportEnabled = isWeeklyReportEnabled
)
