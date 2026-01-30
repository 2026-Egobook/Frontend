package com.egobook.app.domain.usecase

import androidx.paging.PagingData
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswerItem
import com.egobook.app.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyRepliesHistoryUseCase @Inject constructor(private val repository: QuestionRepository) {
    suspend operator fun invoke(size: Int): Flow<PagingData<MyTodayQuestionAnswerItem>> = repository.fetchMyRepliesHistory(size = size)
}
