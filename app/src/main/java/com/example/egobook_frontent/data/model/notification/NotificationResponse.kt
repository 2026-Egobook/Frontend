package com.example.egobook_frontent.data.model.notification

import com.example.egobook_frontent.domain.model.Notification

data class NotificationResponse(
    val isEnabled: Boolean
)

fun NotificationResponse.toDomain(): Notification = Notification(
    isEnabled = isEnabled
)
