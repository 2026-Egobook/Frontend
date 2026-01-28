package com.egobook.app.domain.repository

import androidx.paging.PagingData
import com.egobook.app.domain.model.TodayQuestion
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswerItem
import com.egobook.app.domain.model.square.question.TodayAnswer
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun fetchTodayQuestion(isSubmit: Boolean): Result<TodayQuestion>
    suspend fun submitTodayAnswer(answer: TodayAnswer): Result<Unit>
    fun fetchMyRepliesHistory(size: Int): Flow<PagingData<MyTodayQuestionAnswerItem>>
}

