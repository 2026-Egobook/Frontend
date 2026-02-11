package com.egobook.app.domain.usecase.egoroom

import com.egobook.app.domain.model.counseling.WeeklyReportUnlockType
import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class UnlockWeeklyReportUseCase @Inject constructor(private val repository: CounselingRepository) {
    suspend operator fun invoke(startDate: String, unlockType: WeeklyReportUnlockType) = repository.unlockWeeklyReport(startDate, unlockType)
}