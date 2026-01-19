package com.example.egobook_frontent.ui.home

sealed class NotificationPublisher {
    object Admin : NotificationPublisher()
    data class User(val userId: String, val userName: String) : NotificationPublisher()
}
