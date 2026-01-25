package com.egobook.app.data.repository

import com.egobook.app.data.api.QuestionApiService
import com.egobook.app.data.model.square.question.TodayQuestionResponse
import com.egobook.app.data.model.square.question.toDomain
import com.egobook.app.domain.model.TodayQuestion
import com.egobook.app.domain.repository.QuestionRepository
import javax.inject.Inject

class QuestionRepositoryImpl @Inject constructor(private val apiService: QuestionApiService): QuestionRepository {
    override suspend fun fetchTodayQuestion(): Result<TodayQuestion> = try {
//        val response = apiService.fetchTodayQuestion()
//        if(response.status == 200) {
//            Result.success(response.data.toDomain())
//        } else {
//            Result.failure(Exception("Error: ${response.status}"))
//        }
        val mockTodayQuestion = TodayQuestionResponse(
            questionId = 101L,
            content = "오늘 가장 집중이 잘 됐던 순간은 언제였나요?",
            date = "2026-01-19",
            isUserAnswered = true
        )
        Result.success(mockTodayQuestion.toDomain())
    } catch (e: Exception) {
        Result.failure(e)
    }
}