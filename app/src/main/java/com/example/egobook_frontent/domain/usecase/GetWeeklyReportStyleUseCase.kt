package com.example.egobook_frontent.domain.usecase

import com.example.egobook_frontent.domain.model.WeeklyReportStyle
import com.example.egobook_frontent.domain.repository.CounselingRepository
import javax.inject.Inject

class GetWeeklyReportStyleUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    suspend operator fun invoke(): Result<WeeklyReportStyle> = repository.getWeeklyReportStyle()

}