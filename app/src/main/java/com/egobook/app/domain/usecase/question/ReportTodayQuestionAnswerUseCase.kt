package com.egobook.app.domain.usecase.question

import com.egobook.app.domain.model.square.letter.ReportContent
import com.egobook.app.domain.repository.QuestionRepository
import javax.inject.Inject

class ReportTodayQuestionAnswerUseCase @Inject constructor(private val repository: QuestionRepository) {
    suspend operator fun invoke(answerId: Long, request: ReportContent): Result<Unit> = repository.reportTodayQuestionAnswer(answerId = answerId, request = request)
}