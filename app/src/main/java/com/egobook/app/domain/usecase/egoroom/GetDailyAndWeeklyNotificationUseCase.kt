package com.egobook.app.domain.usecase.egoroom

import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class GetDailyAndWeeklyNotificationUseCase @Inject constructor(private val repository: CounselingRepository) {
    suspend operator fun invoke() = repository.getDailyAndWeeklyNotification()
}