package com.egobook.app.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egobook.app.data.api.QuestionApiService
import com.egobook.app.data.model.square.question.TodayAnswerRequest
import com.egobook.app.data.model.square.question.toDomain
import com.egobook.app.data.repository.paging.AllUserRepliesPagingSource
import com.egobook.app.data.repository.paging.FriendRepliesPagingSource
import com.egobook.app.data.repository.paging.MyRepliesHistoryPagingSource
import com.egobook.app.domain.model.TodayQuestion
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswerItem
import com.egobook.app.domain.model.square.question.TodayAnswer
import com.egobook.app.domain.model.square.question.UserTodayQuestionAnswerItem
import com.egobook.app.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(private val apiService: QuestionApiService) :
    QuestionRepository {

    override suspend fun fetchTodayQuestion(): Result<TodayQuestion> = try {
        val response = apiService.fetchTodayQuestion()
        if(response.status == 200) {
            Result.success(response.data.toDomain())
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun submitTodayAnswer(answer: TodayAnswer): Result<Unit> = try {
        val response = apiService.submitTodayAnswer(
            answer = TodayAnswerRequest(
                content = answer.content,
                visibilityType = answer.visibilityType
            )
        )
        if (response.status == 200) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun updateTodayAnswer(updatedAnswer: TodayAnswer): Result<Unit> = try {
        val response = apiService.updateTodayAnswer(updatedAnswer = TodayAnswerRequest(content = updatedAnswer.content, visibilityType = updatedAnswer.visibilityType))
        if(response.status == 200) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * 1. Paging3는 첫 번째 호출일 때, pageSize의 3배를 호출한다.
     * 그 이후 다음부터는 정의했던 pageSize만큼 호출한다.
     * initialLoadSize 속성을 따로 설정하면 첫 호출때도 원래 사이즈의 3배가 아닌 기존 사이즈만큼 불러온다.
     * enablePlaceholders: 데이터가 아직 로드되지 않은 부분에 Skeleton UI(Placeholder)을 미리 만들어줄 것인가?
     * 서버에서 전체 데이터 개수를 받아올 수 있고, 부드러운 스크롤 경헙과 스켈레톤 UI를 제공하고 싶다면 true
     * 전체 개수를 알 지 못하고 무한 스크롤 형태를 원한다면 false 사용
     * config: 데이터를 어떻게 가져올 것인가?
     * pagingSourceFactory: 데이터를 어디서 가져올 것인가?
     */
    override fun fetchMyRepliesHistory(size: Int): Flow<PagingData<MyTodayQuestionAnswerItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                initialLoadSize = size,
                enablePlaceholders = false
            ), // 1
            pagingSourceFactory = {
                MyRepliesHistoryPagingSource(apiService = apiService)
            }
        ).flow
    }

    override fun fetchTodayFriendsReplies(size: Int): Flow<PagingData<UserTodayQuestionAnswerItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                initialLoadSize = size,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                FriendRepliesPagingSource(apiService = apiService)
            }
        ).flow
    }

    override fun fetchTodayAllUserReplies(size: Int): Flow<PagingData<UserTodayQuestionAnswerItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = size,
                initialLoadSize = size,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                AllUserRepliesPagingSource(apiService = apiService)
            }
        ).flow
    }

    override suspend fun deleteMyQuestionAnswer(answerId: Long): Result<Unit> = try {
        val response = apiService.deleteMyQuestionAnswer(answerId = answerId)
        if(response.status == 200) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Error: ${response.status}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

}