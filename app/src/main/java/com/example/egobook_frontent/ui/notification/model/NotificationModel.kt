package com.example.egobook_frontent.ui.notification.model

import com.example.egobook_frontent.domain.model.Notification

data class NotificationModel(
    val isDailyPraiseEnabled: Boolean,
    val isWeeklyReportEnabled: Boolean
)

fun Notification.toPresentation(): NotificationModel = NotificationModel(
    isDailyPraiseEnabled = isDailyPraiseEnabled,
    isWeeklyReportEnabled = isWeeklyReportEnabled
)
