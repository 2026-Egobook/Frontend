package com.egobook.app.domain.repository

import com.egobook.app.domain.model.Notification
import com.egobook.app.domain.model.NotificationType

interface NotificationRepository {
    suspend fun getNotificationStatus(): Result<Notification>
    suspend fun updateNotificationStatus(type: NotificationType, isEnabled: Boolean): Result<Boolean>
}