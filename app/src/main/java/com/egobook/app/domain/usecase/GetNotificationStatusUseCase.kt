package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.Notification
import com.egobook.app.domain.repository.NotificationRepository
import javax.inject.Inject

class GetNotificationStatusUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(): Result<Notification> = repository.getNotificationStatus()
}