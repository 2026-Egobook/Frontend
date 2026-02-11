package com.egobook.app.domain.usecase.egoroom

import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class UpdateDailyPraiseNotificationUseCase @Inject constructor(private val repository: CounselingRepository) {
    suspend operator fun invoke(isEnabled: Boolean): Result<Boolean> = repository.updateDailyPraiseNotification(isEnabled = isEnabled)
}