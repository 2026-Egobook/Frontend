package com.example.egobook_frontent.domain.usecase

import com.example.egobook_frontent.domain.model.WeeklyReport
import com.example.egobook_frontent.domain.repository.CounselingRepository
import javax.inject.Inject

class GetWeeklyReportUseCase @Inject constructor(private val repository: CounselingRepository) {
    suspend operator fun invoke(): Result<List<WeeklyReport>> = repository.getWeeklyReport()
}