package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.ReportStyle
import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class UpdateWeeklyReportStyleUseCase @Inject constructor(
    private val repository: CounselingRepository
){
    suspend operator fun invoke(reportStyle: ReportStyle): Result<ReportStyle> = repository.updateWeeklyReportStyle(reportStyle = reportStyle)
}