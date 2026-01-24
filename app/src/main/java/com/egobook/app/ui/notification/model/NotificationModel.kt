package com.egobook.app.ui.notification.model

import com.egobook.app.domain.model.Notification

data class NotificationModel(
    val isDailyPraiseEnabled: Boolean,
    val isWeeklyReportEnabled: Boolean
)

fun Notification.toPresentation(): NotificationModel = NotificationModel(
    isDailyPraiseEnabled = isDailyPraiseEnabled,
    isWeeklyReportEnabled = isWeeklyReportEnabled
)
