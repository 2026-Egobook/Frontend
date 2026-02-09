package com.egobook.app.domain.usecase.egoroom

import com.egobook.app.domain.repository.CounselingRepository
import javax.inject.Inject

class GetDailyPraiseByDateUseCase @Inject constructor(private val repository: CounselingRepository) {
    suspend operator fun invoke(date: String) = repository.getDailyPraiseByDate(date = date)
}