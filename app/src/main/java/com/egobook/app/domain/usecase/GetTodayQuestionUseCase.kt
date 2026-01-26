package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.TodayQuestion
import com.egobook.app.domain.repository.QuestionRepository
import javax.inject.Inject

class GetTodayQuestionUseCase @Inject constructor(private val repository: QuestionRepository) {
    suspend operator fun invoke(isSubmit: Boolean): Result<TodayQuestion> = repository.fetchTodayQuestion(isSubmit)
}