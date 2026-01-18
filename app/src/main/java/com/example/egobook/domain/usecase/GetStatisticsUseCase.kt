package com.example.egobook.domain.usecase

import com.example.egobook.domain.model.Statistics
import com.example.egobook.domain.repository.CounselingRepository
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    suspend operator fun invoke(): Result<Statistics> = repository.getStatistics()

}