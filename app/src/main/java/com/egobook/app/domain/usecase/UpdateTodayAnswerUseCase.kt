package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.square.question.TodayAnswer
import com.egobook.app.domain.repository.QuestionRepository
import javax.inject.Inject

class UpdateTodayAnswerUseCase @Inject constructor(private val repository: QuestionRepository) {
    suspend operator fun invoke(updatedAnswer: TodayAnswer) = repository.updateTodayAnswer(updatedAnswer = updatedAnswer)
}