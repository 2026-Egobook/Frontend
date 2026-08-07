package com.egobook.app.domain.repository

import androidx.paging.PagingData
import com.egobook.app.domain.model.TodayQuestion
import com.egobook.app.domain.model.square.letter.ReportContent
import com.egobook.app.domain.model.square.question.UserTodayQuestionAnswerItem
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswerItem
import com.egobook.app.domain.model.square.question.TodayAnswer
import kotlinx.coroutines.flow.Flow

interface QuestionRepository {
    suspend fun fetchTodayQuestion(): Result<TodayQuestion>
    suspend fun submitTodayAnswer(answer: TodayAnswer): Result<Unit>
    fun fetchMyRepliesHistory(size: Int): Flow<PagingData<MyTodayQuestionAnswerItem>>
    fun fetchTodayFriendsReplies(size: Int): Flow<PagingData<UserTodayQuestionAnswerItem>>
    fun fetchTodayAllUserReplies(size: Int): Flow<PagingData<UserTodayQuestionAnswerItem>>
    suspend fun updateTodayAnswer(updatedAnswer: TodayAnswer): Result<Unit>
    suspend fun deleteMyQuestionAnswer(answerId: Long): Result<Unit>
    suspend fun reportTodayQuestionAnswer(answerId: Long, request: ReportContent): Result<Unit>
    suspend fun updateMarketingConsent(enabled: Boolean): Result<Unit>
}

