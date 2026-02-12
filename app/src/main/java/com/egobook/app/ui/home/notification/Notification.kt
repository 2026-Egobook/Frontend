package com.egobook.app.ui.home.notification

import com.egobook.app.ui.home.notification.NotificationPublisher
import com.egobook.app.ui.home.notification.NotificationStatus

data class Notification(
    val id: Int,
    val content: String,
    val type: NotificationType,
    val status: NotificationStatus,
    val publisher: NotificationPublisher,
    val publishedDate: NotificationTime
)
