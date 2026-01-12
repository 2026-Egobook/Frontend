package com.example.egobook_frontent.domain.usecase

import com.example.egobook_frontent.domain.repository.NotificationRepository
import javax.inject.Inject

class UpdateNotificationUseCase @Inject constructor(
    private val repository: NotificationRepository
) {
    suspend operator fun invoke(isEnabled: Boolean): Result<Boolean> = repository.updateNotificationStatus(isEnabled = isEnabled)
}