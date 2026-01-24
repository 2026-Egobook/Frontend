package com.egobook.app.data.api

import com.egobook.app.data.model.notification.NotificationResponse
import com.egobook.app.domain.model.NotificationType
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH

interface NotificationApiService {
    @GET("api/notification/settings")
    suspend fun getNotificationStatus(): Response<NotificationResponse>

    @PATCH("api/notification/settings")
    suspend fun updateNotificationStatus(type: NotificationType, isEnabled: Boolean): Response<Unit>
}