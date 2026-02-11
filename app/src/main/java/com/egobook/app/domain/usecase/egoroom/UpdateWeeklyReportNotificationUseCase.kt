package com.egobook.app.domain.usecase.egoroom

import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class UpdateWeeklyReportNotificationUseCase @Inject constructor(private val repository: CounselingRepository) {
    suspend operator fun invoke(isEnabled: Boolean) = repository.updateWeeklyReportNotification(isEnabled = isEnabled)
}