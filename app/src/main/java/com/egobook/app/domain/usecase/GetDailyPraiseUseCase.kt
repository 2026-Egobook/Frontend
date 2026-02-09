package com.egobook.app.domain.usecase

import androidx.paging.PagingData
import com.egobook.app.domain.model.PraiseMessage
import com.egobook.app.domain.model.counseling.PraiseDailyItem
import com.egobook.app.domain.repository.CounselingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDailyPraiseUseCase @Inject constructor(
    private val repository: CounselingRepository
) {
    operator fun invoke(size: Int): Flow<PagingData<PraiseDailyItem>> = repository.getDailyPraise(size = size)
}