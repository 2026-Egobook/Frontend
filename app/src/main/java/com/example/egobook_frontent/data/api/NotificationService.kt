package com.example.egobook_frontent.data.api

import com.example.egobook_frontent.data.model.notification.NotificationResponse
import com.example.egobook_frontent.domain.model.NotificationType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH

interface NotificationService {
    @GET("api/notification/settings")
    suspend fun getNotificationStatus(): Response<NotificationResponse>

    @PATCH("api/notification/settings")
    suspend fun updateNotificationStatus(type: NotificationType, isEnabled: Boolean): Response<Unit>
}