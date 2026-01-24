package com.example.egobook_frontent.domain.repository

import com.example.egobook_frontent.domain.model.Notification
import com.example.egobook_frontent.domain.model.NotificationType

interface NotificationRepository {
    suspend fun getNotificationStatus(): Result<Notification>
    suspend fun updateNotificationStatus(type: NotificationType, isEnabled: Boolean): Result<Boolean>
}