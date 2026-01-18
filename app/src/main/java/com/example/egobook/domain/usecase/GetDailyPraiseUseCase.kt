package com.example.egobook.domain.usecase

import com.example.egobook.domain.model.PraiseMessage
import com.example.egobook.domain.repository.CounselingRepository
import javax.inject.Inject

class GetDailyPraiseUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    suspend operator fun invoke(): Result<List<PraiseMessage>> = repository.getDailyPraise()
}