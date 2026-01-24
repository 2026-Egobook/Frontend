package com.example.egobook_frontent.domain.usecase

import com.example.egobook_frontent.domain.model.Notification
import com.example.egobook_frontent.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationStatusUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): Result<Notification> = repository.getNotificationStatus()
}