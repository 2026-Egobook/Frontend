package com.egobook.app.domain.usecase.egoroom

import androidx.paging.PagingData
import com.egobook.app.domain.model.counseling.DailyPraise
import com.egobook.app.domain.repository.CounselingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyPraiseUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    operator fun invoke(size: Int): Flow<PagingData<DailyPraise>> = repository.getDailyPraise(size = size)
}