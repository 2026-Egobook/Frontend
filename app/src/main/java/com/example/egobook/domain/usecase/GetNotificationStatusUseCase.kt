package com.example.egobook.domain.usecase

import com.example.egobook.domain.model.Notification
import com.example.egobook.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationStatusUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): Result<Notification> = repository.getNotificationStatus()
}