package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.NotificationType
import com.egobook.app.domain.repository.NotificationRepository
import javax.inject.Inject

class UpdateNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(type: NotificationType, isEnabled: Boolean): Result<Boolean> = repository.updateNotificationStatus(type = type, isEnabled = isEnabled)
}