package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.Statistics
import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    suspend operator fun invoke(): Result<Statistics> = repository.getStatistics()

}