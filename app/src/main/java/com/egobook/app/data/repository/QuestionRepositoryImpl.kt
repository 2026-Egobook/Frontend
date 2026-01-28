package com.egobook.app.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.egobook.app.data.api.QuestionApiService
import com.egobook.app.data.model.square.question.TodayQuestionAnswerResponse
import com.egobook.app.data.model.square.question.TodayQuestionResponse
import com.egobook.app.data.model.square.question.toDomain
import com.egobook.app.data.repository.paging.QuestionPagingSource
import com.egobook.app.domain.model.TodayQuestion
import com.egobook.app.domain.model.square.question.AnswerVisibility
import com.egobook.app.domain.model.square.question.MyTodayQuestionAnswerItem
import com.egobook.app.domain.model.square.question.TodayAnswer
import com.egobook.app.domain.repository.QuestionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(private val apiService: QuestionApiService): QuestionRepository {

    override suspend fun fetchTodayQuestion(isSubmit: Boolean): Result<TodayQuestion> = try {
//        val response = apiService.fetchTodayQuestion()
//        if(response.status == 200) {
//            Result.success(response.data.toDomain())
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        if(isSubmit) {
            val mockTodayQuestion = TodayQuestionResponse(
                questionId = 101L,
                content = "오늘 가장 집중이 잘 됐던 순간은 언제였나요?",
                date = "2026-01-19",
                isUserAnswered = true,
                myAnswer = TodayQuestionAnswerResponse(
                    answerId = 9007199254740991L,
                    content = "오전 10시쯤 커피 마시면서 코딩할 때가 가장 집중이 잘 되었어요.",
                    visibility = AnswerVisibility.PUBLIC,
                    answeredAt = "2026-01-28T04:11:36.224Z"
                )
            )
            Result.success(mockTodayQuestion.toDomain())
        } else {
            val mockTodayQuestion = TodayQuestionResponse(
                questionId = 101L,
                content = "오늘 가장 집중이 잘 됐던 순간은 언제였나요?",
                date = "2026-01-19",
                isUserAnswered = false,
                myAnswer = null
            )
            Result.success(mockTodayQuestion.toDomain())
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun submitTodayAnswer(answer: TodayAnswer): Result<Unit> = try {
//        val response = apiService.submitTodayAnswer(answer = TodayAnswerRequest(content = answer.content, visibilityType = answer.visibilityType.value))
//        if(response.status == 200) {
//            Result.success(Unit)
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /**
     * 1. Paging3는 첫 번째 호출일 때, pageSize의 3배를 호출한다.
     * 그 이후 다음부터는 정의했던 pageSize만큼 호출한다.
     * initialLoadSize 속성을 따로 설정하면 첫 호출때도 원래 사이즈의 3배가 아닌 기존 사이즈만큼 불러온다.
     */
    override fun fetchMyRepliesHistory(size: Int): Flow<PagingData<MyTodayQuestionAnswerItem>> {
        return Pager(
            config = PagingConfig(pageSize = size, initialLoadSize = size, enablePlaceholders = false), // 1
            pagingSourceFactory = {
                QuestionPagingSource(apiService = apiService)
            }
        ).flow
    }

}