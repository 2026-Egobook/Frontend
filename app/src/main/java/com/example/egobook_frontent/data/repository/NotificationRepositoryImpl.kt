package com.example.egobook_frontent.data.repository

import com.example.egobook_frontent.data.api.NotificationService
import com.example.egobook_frontent.data.model.notification.toDomain
import com.example.egobook_frontent.domain.model.Notification
import com.example.egobook_frontent.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(private val apiService: NotificationService): NotificationRepository {
    override suspend fun getNotificationStatus(): Result<Notification> = try {
        val response = apiService.getNotificationStatus()
        if(response.isSuccessful && response.body()!=null) {
            Result.success(response.body()!!.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateNotificationStatus(isEnabled: Boolean): Result<Boolean> = try {
        val response = apiService.updateNotificationStatus(isEnabled = isEnabled)
        if(response.isSuccessful) {
            Result.success(isEnabled)
        } else {
            Result.failure(Exception("Error: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}