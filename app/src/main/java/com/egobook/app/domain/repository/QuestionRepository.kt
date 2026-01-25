package com.egobook.app.domain.repository

import com.egobook.app.domain.model.TodayQuestion

interface QuestionRepository {
    suspend fun fetchTodayQuestion(): Result<TodayQuestion>
}

