package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.WeeklyReportStyle
import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class GetWeeklyReportStyleUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    suspend operator fun invoke(): Result<WeeklyReportStyle> = repository.getWeeklyReportStyle()

}