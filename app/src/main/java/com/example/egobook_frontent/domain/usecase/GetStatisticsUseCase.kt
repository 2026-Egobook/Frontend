package com.example.egobook_frontent.domain.usecase

import com.example.egobook_frontent.domain.model.Statistics
import com.example.egobook_frontent.domain.repository.CounselingRepository
import javax.inject.Inject

class GetStatisticsUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    suspend operator fun invoke(): Result<Statistics> = repository.getStatistics()

}