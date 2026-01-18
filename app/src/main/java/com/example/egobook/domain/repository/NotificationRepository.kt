package com.example.egobook.domain.repository

import com.example.egobook.domain.model.Notification
import com.example.egobook.domain.model.NotificationType

interface NotificationRepository {
    suspend fun getNotificationStatus(): Result<Notification>
    suspend fun updateNotificationStatus(type: NotificationType, isEnabled: Boolean): Result<Boolean>
}