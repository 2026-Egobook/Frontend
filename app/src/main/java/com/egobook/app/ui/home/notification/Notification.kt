package com.egobook.app.ui.home.notification

data class Notification(
    val id: Int,
    val content: String,
    val type: NotificationType,
    val status: NotificationStatus,
    val publisher: NotificationPublisher,
    val publishedDate: NotificationTime
)
