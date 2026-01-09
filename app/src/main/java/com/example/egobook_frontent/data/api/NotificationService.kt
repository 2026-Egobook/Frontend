package com.example.egobook_frontent.data.api

import com.example.egobook_frontent.data.model.notification.NotificationResponse
import retrofit2.Response
import retrofit2.http.GET

interface NotificationService {
    @GET("api/notification/settings")
    suspend fun getNotificationStatus(): Response<NotificationResponse>
}