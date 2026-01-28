package com.egobook.app.domain.usecase

import androidx.paging.PagingData
import com.egobook.app.domain.model.square.question.FriendTodayQuestionAnswerItem
import com.egobook.app.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTodayFriendsRepliesUseCase @Inject constructor(
    private val repository: QuestionRepository
) {
    operator fun invoke(size: Int): Flow<PagingData<FriendTodayQuestionAnswerItem>> = repository.fetchTodayFriendsReplies(size = size)
}