package com.example.egobook_frontent.domain.repository

import com.example.egobook_frontent.domain.model.Notification

interface NotificationRepository {
    suspend fun getNotificationStatus(): Result<Notification>
    suspend fun updateNotificationStatus(isEnabled: Boolean): Result<Boolean>
}