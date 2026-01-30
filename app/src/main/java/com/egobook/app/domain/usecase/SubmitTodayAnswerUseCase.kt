package com.egobook.app.domain.usecase

import com.egobook.app.domain.model.square.question.TodayAnswer
import com.egobook.app.domain.repository.QuestionRepository
import javax.inject.Inject

class SubmitTodayAnswerUseCase @Inject constructor(private val questionRepository: QuestionRepository) {
    suspend operator fun invoke(answer: TodayAnswer) = questionRepository.submitTodayAnswer(answer)
}