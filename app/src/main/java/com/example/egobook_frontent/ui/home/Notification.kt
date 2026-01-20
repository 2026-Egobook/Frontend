package com.example.egobook_frontent.ui.home

data class Notification(
    val content: String,
    val type: NotificationType,
    val status: NotificationStatus,
    val publisher: NotificationPublisher,
    val publishedDate: NotificationTime
)
