package com.egobook.app.data.repository

import com.egobook.app.data.api.NotificationApiService
import com.egobook.app.domain.model.Notification
import com.egobook.app.domain.model.NotificationType
import com.egobook.app.domain.repository.NotificationRepository
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(private val apiService: NotificationApiService): NotificationRepository {
    override suspend fun getNotificationStatus(): Result<Notification> = try {
//        val response = apiService.getNotificationStatus()
//        if(response.isSuccessful && response.body() != null) {
//            Result.success(response.body()!!.toDomain())
//        } else {
//            Result.failure(Exception("Error: ${response.code()}"))
//        }
        Result.success(Notification(isDailyPraiseEnabled = true, isWeeklyReportEnabled = true))
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateNotificationStatus(type: NotificationType, isEnabled: Boolean): Result<Boolean> = try {
//        val response = apiService.updateNotificationStatus(type = type, isEnabled = isEnabled)
//        if(response.isSuccessful) {
//            Result.success(isEnabled)
//        } else {
//            Result.failure(Exception("Error: ${response.code()}"))
//        }
        Result.success(isEnabled)
    } catch (e: Exception) {
        Result.failure(e)
    }
}