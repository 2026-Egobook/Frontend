package com.egobook.app.ui.home.notification

sealed class NotificationPublisher {
    object Admin : NotificationPublisher()
    data class User(val userId: String, val userName: String) : NotificationPublisher()
}
