package com.example.egobook.data.api

import com.example.egobook.data.model.notification.NotificationResponse
import com.example.egobook.domain.model.NotificationType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH

interface NotificationApiService {
    @GET("api/notification/settings")
    suspend fun getNotificationStatus(): Response<NotificationResponse>

    @PATCH("api/notification/settings")
    suspend fun updateNotificationStatus(type: NotificationType, isEnabled: Boolean): Response<Unit>
}