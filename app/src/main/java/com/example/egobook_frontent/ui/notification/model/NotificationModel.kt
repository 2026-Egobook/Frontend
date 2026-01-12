package com.example.egobook_frontent.ui.notification.model

import com.example.egobook_frontent.domain.model.Notification

data class NotificationModel(
    val isEnabled: Boolean
)

fun Notification.toPresentation(): NotificationModel = NotificationModel(
    isEnabled = isEnabled
)
