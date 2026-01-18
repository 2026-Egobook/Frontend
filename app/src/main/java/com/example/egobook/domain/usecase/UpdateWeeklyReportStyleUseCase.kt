package com.example.egobook.domain.usecase

import com.example.egobook.domain.model.ReportStyle
import com.example.egobook.domain.repository.CounselingRepository
import javax.inject.Inject

class UpdateWeeklyReportStyleUseCase @Inject constructor(
    private val repository: CounselingRepository
){
    suspend operator fun invoke(reportStyle: ReportStyle): Result<ReportStyle> = repository.updateWeeklyReportStyle(reportStyle = reportStyle)
}