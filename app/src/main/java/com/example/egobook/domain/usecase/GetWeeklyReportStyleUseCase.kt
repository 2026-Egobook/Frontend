package com.example.egobook.domain.usecase

import com.example.egobook.domain.model.WeeklyReportStyle
import com.example.egobook.domain.repository.CounselingRepository
import javax.inject.Inject

class GetWeeklyReportStyleUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    suspend operator fun invoke(): Result<WeeklyReportStyle> = repository.getWeeklyReportStyle()

}