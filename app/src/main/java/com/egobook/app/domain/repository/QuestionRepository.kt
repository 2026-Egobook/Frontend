package com.egobook.app.domain.repository

import com.egobook.app.domain.model.TodayQuestion
import com.egobook.app.domain.model.square.question.TodayAnswer

interface QuestionRepository {
    suspend fun fetchTodayQuestion(isSubmit: Boolean): Result<TodayQuestion>
    suspend fun submitTodayAnswer(answer: TodayAnswer): Result<Unit>
}

