package com.egobook.app.domain.usecase

import androidx.paging.PagingData
import com.egobook.app.domain.model.square.question.UserTodayQuestionAnswerItem
import com.egobook.app.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayAllUserRepliesUseCase @Inject constructor(private val repository: QuestionRepository) {
    operator fun invoke(size: Int): Flow<PagingData<UserTodayQuestionAnswerItem>> = repository.fetchTodayAllUserReplies(size = size)
}