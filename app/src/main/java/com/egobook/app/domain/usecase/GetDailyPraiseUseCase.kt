package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.PraiseMessage
import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class GetDailyPraiseUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    suspend operator fun invoke(): Result<List<PraiseMessage>> = repository.getDailyPraise()
}