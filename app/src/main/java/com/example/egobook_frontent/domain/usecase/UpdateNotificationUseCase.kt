package com.example.egobook_frontent.domain.usecase

import com.example.egobook_frontent.domain.model.NotificationType
import com.example.egobook_frontent.domain.repository.NotificationRepository
import javax.inject.Inject

class UpdateNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(type: NotificationType, isEnabled: Boolean): Result<Boolean> = repository.updateNotificationStatus(type = type, isEnabled = isEnabled)
}