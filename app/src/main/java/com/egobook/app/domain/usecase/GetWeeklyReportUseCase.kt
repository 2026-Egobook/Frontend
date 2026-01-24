package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.WeeklyReport
import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class GetWeeklyReportUseCase @Inject constructor(private val repository: CounselingRepository) {
    suspend operator fun invoke(): Result<List<WeeklyReport>> = repository.getWeeklyReport()
}