package com.egobook.app.domain.usecase.question

import com.egobook.app.domain.model.square.question.AnswerEmpathy
import com.egobook.app.domain.repository.QuestionRepository
import javax.inject.Inject

class ToggleAnswerEmpathyUseCase @Inject constructor(private val repository: QuestionRepository) {
    suspend operator fun invoke(answerId: Long): Result<AnswerEmpathy> = repository.toggleAnswerEmpathy(answerId = answerId)
}
