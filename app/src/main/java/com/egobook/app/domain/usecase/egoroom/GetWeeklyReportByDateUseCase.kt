package com.egobook.app.domain.usecase.egoroom

import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class GetWeeklyReportByDateUseCase @Inject constructor(private val repository: CounselingRepository) {
    suspend operator fun invoke(startDate: String) = repository.getWeeklyReportByDate(startDate = startDate)
}
