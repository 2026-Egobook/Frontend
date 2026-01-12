package com.example.egobook_frontent.domain.usecase

import com.example.egobook_frontent.domain.model.PraiseMessage
import com.example.egobook_frontent.domain.repository.CounselingRepository
import javax.inject.Inject

class GetDailyPraiseUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    suspend operator fun invoke(): Result<List<PraiseMessage>> = repository.getDailyPraise()
}