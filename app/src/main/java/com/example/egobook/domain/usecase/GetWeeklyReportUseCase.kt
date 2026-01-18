package com.example.egobook.domain.usecase

import com.example.egobook.domain.model.WeeklyReport
import com.example.egobook.domain.repository.CounselingRepository
import javax.inject.Inject

class GetWeeklyReportUseCase @Inject constructor(private val repository: CounselingRepository) {
    suspend operator fun invoke(): Result<List<WeeklyReport>> = repository.getWeeklyReport()
}