package com.egobook.app.domain.usecase.egoroom

import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.model.WeeklyReportStyle
import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class GetWeeklyReportStyleUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    suspend operator fun invoke(): Result<ReportStyle> = repository.getWeeklyReportStyle()

}