package com.egobook.app.domain.usecase

import androidx.paging.PagingData
import com.egobook.app.domain.model.counseling.WeeklyReport
import com.egobook.app.domain.repository.CounselingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWeeklyReportsUseCase @Inject constructor(private val repository: CounselingRepository) {
    operator fun invoke(size: Int): Flow<PagingData<WeeklyReport>> = repository.getWeeklyReports(size = size)
}