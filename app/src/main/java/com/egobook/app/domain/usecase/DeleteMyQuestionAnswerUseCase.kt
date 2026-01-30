package com.egobook.app.domain.usecase

import com.egobook.app.domain.repository.QuestionRepository
import javax.inject.Inject

class DeleteMyQuestionAnswerUseCase @Inject constructor(private val repository: QuestionRepository) {
    suspend operator fun invoke(answerId: Long): Result<Unit> = repository.deleteMyQuestionAnswer(answerId = answerId)
}